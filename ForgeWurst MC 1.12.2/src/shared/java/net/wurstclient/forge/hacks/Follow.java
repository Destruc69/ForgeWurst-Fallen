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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class Follow extends Hack {

	private final SliderSetting range =
			new SliderSetting("Range", 8, 1.0, 50, 1.0, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting dis =
			new CheckboxSetting("DistanceToTarget",
					false);

	ArrayList<AxisAlignedBB> bbs = new ArrayList<>();

	Entity entityy;

	private Entity entity;
	boolean a = false;

	public Follow() {
		super("Follow", "Bot that follows entitys.");
		setCategory(Category.MOVEMENT);
		addSetting(range);
		addSetting(dis);
	}

	@Override
	public String getRenderName() {
		return getName() + " [" + range.getValueString() + "]";
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (Entity e : mc.world.loadedEntityList) {
			if (e instanceof Entity) {
				if (e != mc.player) {
					if (mc.player.getDistance(e) < range.getValue() && !mc.player.collidedHorizontally) {

						entityy = e;
						bbs.add(BlockUtils.getBoundingBox(e.getPosition().add(0, -1, 0)));

						double dd = RotationUtils.getEyesPos().distanceTo(
								e.getEntityBoundingBox().getCenter());
						double posXX = e.lastTickPosX + (e.lastTickPosX - e.lastTickPosX) * dd
								- mc.player.posX;
						double posYY = e.lastTickPosY + (e.lastTickPosY - e.lastTickPosY) * dd
								+ e.getEyeHeight() * 0.5 - mc.player.posY
								- mc.player.getEyeHeight();
						double posZZ = e.lastTickPosZ + (e.lastTickPosZ - e.lastTickPosZ) * dd
								- mc.player.posZ;

						KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindAttack, false);

						double posxe = e.posX;
						double posze = e.posZ;

						mc.player.rotationYaw = (float) Math.toDegrees(Math.atan2(posZZ, posXX)) - 90;

						if (mc.player.posX == posxe && mc.player.posZ == posze) {
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
						} else {
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
						}

						if (dis.isChecked()) {
							double dis = mc.player.getDistance(e);
							ChatUtils.message("Distance:" + " " + Math.round(dis));
							ForgeWurst.getForgeWurst().getHax().follow.dis.setChecked(false);
						}
					} else {
						bbs.clear();
					}
				}
			} else if (e == null) {
				double ra = range.getValue();
				ChatUtils.error("No Entity Found in range of" + " " + ra + "," + " " + "Please adjust the value of the range in the setting, Or wait for an entity to enter range");
				break;
			}
		}

		if (mc.player.moveForward < 0 && mc.player.moveStrafing < 0) {
			if (mc.player.onGround) {
				mc.player.jump();
			}
		}

		if (mc.player.collidedHorizontally && !mc.player.isInWater()) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindAttack, true);
			mc.player.rotationPitch = 50;
		}

		if (mc.player.isInWater()) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
		} else {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		}
	}
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		try {
			Vec3d start = RotationUtils.getClientLookVec()
					.addVector(0, 0, 0)
					.addVector(TileEntityRendererDispatcher.staticPlayerX,
							TileEntityRendererDispatcher.staticPlayerY,
							TileEntityRendererDispatcher.staticPlayerZ);

			Vec3d end = entityy.getEntityBoundingBox().getCenter()
					.subtract(new Vec3d(entityy.posX, entityy.posY, entityy.posZ)
							.subtract(entityy.prevPosX, entityy.prevPosY, entityy.prevPosZ)
							.scale(1 - event.getPartialTicks()));


			// GL settings
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glLineWidth(2);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			GL11.glPushMatrix();
			GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
					-TileEntityRendererDispatcher.staticPlayerY,
					-TileEntityRendererDispatcher.staticPlayerZ);

			GL11.glColor4f(1, 0, 0, 0.5F);
			GL11.glBegin(GL11.GL_LINES);
			RenderUtils.drawArrow(start, end);
			GL11.glEnd();


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