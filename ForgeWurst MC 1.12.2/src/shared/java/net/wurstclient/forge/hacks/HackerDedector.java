/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.TimerUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

public final class HackerDedector extends Hack {
	ArrayList<EntityPlayer> hackers = new ArrayList<>();

	ArrayList<String> names = new ArrayList<>();

	public static double limit;
	public double maxLimit = 10;

	World world;

	public static double lastOnGroundPosY;
	public static double lastOnGroundPosX;
	public static double lastOnGroundPosZ;

	public static double currentX;
	public static double currentZ;

	private final CheckboxSetting groundSpoof =
			new CheckboxSetting("GroundSpoof",
					true);

	private final CheckboxSetting motionSpeed =
			new CheckboxSetting("MotionSpeed",
					true);

	private final CheckboxSetting strangeMovement =
			new CheckboxSetting("StrangeMovement",
					true);

	private final CheckboxSetting scaffold =
			new CheckboxSetting("Scaffold",
					true);

	public HackerDedector() {
		super("HackerDetector", "Show and tell you if we suspect hackers in your game other than you.");
		setCategory(Category.PLAYER);
		addSetting(groundSpoof);
		addSetting(motionSpeed);
		addSetting(strangeMovement);
		addSetting(scaffold);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		TimerUtils.reset();
		world = mc.world;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.world != world) {
			names.clear();
			world = mc.world;
		}

		if (mc.world != world)
			return;

		if (mc.player.ticksExisted % 2 == 0) {
			if (limit == 0)
				return;
			limit = limit - 1;
			TimerUtils.reset();
		}
		boolean isAtLimit = limit >= maxLimit;

		if (limit <= 1) {
			limit = limit + 1;
		}

		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityPlayer) {
				if (entity != mc.player) {

					if (entity == mc.player)
						return;

					currentX = entity.lastTickPosX;
					currentZ = entity.lastTickPosZ;

					if (entity.onGround) {
						lastOnGroundPosY = entity.lastTickPosY;
						lastOnGroundPosX = entity.lastTickPosX;
						lastOnGroundPosZ = entity.lastTickPosZ;
					}

					if (strangeMovement.isChecked()) {
						//Strange-Movement[A]
						if (!isAtLimit) {
							if (entity.lastTickPosY > lastOnGroundPosY + 1.15) {
								if (!hackers.contains(entity)) {
									hackers.add((EntityPlayer) entity);
								}
								if (!names.contains(entity.getName())) {
									names.add(entity.getName());
								}
								limit = limit + 1;
								ChatUtils.warning("[HD]" + " " + entity.getName() + " " + "failed Strange-Movement[A]");
							}
						}
					}

					if (strangeMovement.isChecked()) {
						//Strange-Movement[B]
						if (!isAtLimit) {
							if (!entity.onGround || entity.isAirBorne || entity.fallDistance > 0.1 && entity.getDistanceSq(lastOnGroundPosX, entity.lastTickPosY, lastOnGroundPosZ) > 4) {
								if (!hackers.contains(entity)) {
									hackers.add((EntityPlayer) entity);
								}
								if (!names.contains(entity.getName())) {
									names.add(entity.getName());
								}
								limit = limit + 1;
								ChatUtils.warning("[HD]" + " " + entity.getName() + " " + "failed Strange-Movement[B]");
							}
						}
					}

					if (groundSpoof.isChecked()) {
						//Ground-Spoof[A]
						if (!isAtLimit) {
							BlockPos blockPosBelow = new BlockPos(entity.posX, entity.posY - 1, entity.posZ);
							if (mc.world.getBlockState(blockPosBelow).getBlock().equals(Blocks.AIR) && entity.onGround || !entity.isAirBorne || entity.fallDistance < 0.1) {
								if (!hackers.contains(entity)) {
									hackers.add((EntityPlayer) entity);
								}
								if (!names.contains(entity.getName())) {
									names.add(entity.getName());
								}
								limit = limit + 1;
								ChatUtils.warning("[HD]" + " " + entity.getName() + " " + "failed Ground-Spoof[A]");
							}
						}
					}

					if (motionSpeed.isChecked()) {
						//Motion-Speed[A]
						if (!isAtLimit) {
							if (entity.motionX > 2 ||
									entity.motionZ > 2 ||
									entity.motionY > 0.406 ||
									entity.motionX < -2 ||
									entity.motionZ < -2 ||
									entity.motionY < -0.406) {
								if (!hackers.contains(entity)) {
									hackers.add((EntityPlayer) entity);
								}
								if (!names.contains(entity.getName())) {
									names.add(entity.getName());
								}
								limit = limit + 1;
								ChatUtils.warning("[HD]" + " " + entity.getName() + " " + "failed Motion-Speed[A]");
							}
						}
					}
					if (scaffold.isChecked()) {
						//Scaffold[A]
						if (!isAtLimit) {
							if (((EntityPlayer) entity).getHeldItemMainhand().getItem() instanceof ItemBlock && !((EntityPlayer) entity).getHeldItemMainhand().getItem().equals(Blocks.AIR) && ((EntityPlayer) entity).isSwingInProgress && entity.isSprinting() || entity.fallDistance > 0.75) {
								if (!hackers.contains(entity)) {
									hackers.add((EntityPlayer) entity);
								}
								if (!names.contains(entity.getName())) {
									names.add(entity.getName());
								}
								limit = limit + 1;
								ChatUtils.warning("[HD]" + " " + entity.getName() + " " + "failed Scaffold[A]");
							}
						}
					}
				}
			}
		}
	}

	//@SubscribeEvent
	//public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		//String[] hackersName = names.toArray(new String[0]);

		//GL11.glPushMatrix();
		//GL11.glScaled(1.55555555, 1.55555555, 1);
		//WMinecraft.getFontRenderer().drawStringWithShadow(Arrays.toString(hackersName), 8, 8, (int) 0xFF0000);
		//GL11.glPopMatrix();
	//}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		try {
			// GL settings
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glLineWidth(1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			GL11.glPushMatrix();
			GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
					-TileEntityRendererDispatcher.staticPlayerY,
					-TileEntityRendererDispatcher.staticPlayerZ);

			for (Entity e : hackers) {
				GL11.glColor4f(1, 0, 0, 0.2F);
				GL11.glBegin(GL11.GL_QUADS);
				RenderUtils.drawSolidBox(e.getEntityBoundingBox());
				GL11.glEnd();
			}

			GL11.glPopMatrix();

			// GL resets
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}