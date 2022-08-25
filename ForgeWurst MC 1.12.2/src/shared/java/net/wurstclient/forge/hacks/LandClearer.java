/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class LandClearer extends Hack {

	private final SliderSetting addx =
			new SliderSetting("AddX", 2, 0, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting minusy =
			new SliderSetting("MinusY", 2, 0, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting addz =
			new SliderSetting("AddZ", 2, 0, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	public static ArrayList<BlockPos> blocks = new ArrayList<>();

	public LandClearer() {
		super("LandClearer", "Clears land automatically.");
		setCategory(Category.PATHING);
		addSetting(addx);
		addSetting(minusy);
		addSetting(addz);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		for (int x = -addx.getValueI(); x < addx.getValueF(); x++) {
			for (int y = (int) -minusy.getValueF(); y < minusy.getValueF(); y++) {
				for (int z = (int) -addz.getValue(); z < addz.getValueF(); z++) {
					blocks.add(new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z));
				}
			}
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (BlockPos blockPos : blocks) {
			if (mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
				float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				mc.player.rotationYaw = rot[0];
				mc.player.rotationPitch = rot[1];
				if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 3.5) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
					mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.DOWN);
					mc.player.swingArm(EnumHand.MAIN_HAND);
					for (int x = 0; x < 5; x++) {
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					}
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
				}
			}
		}
	}

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

			for (BlockPos posPos : blocks) {
				assert posPos != null;
				GL11.glColor4f(0, 1, 0, 0.5F);
				GL11.glBegin(GL11.GL_LINES);
				RenderUtils.drawOutlinedBox(Objects.requireNonNull(BlockUtils.getBoundingBox(posPos.add(0, -1, 0))));
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