/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class Pointer extends Hack {
	public static ArrayList<BlockPos> blockPos = new ArrayList<>();

	public static int theYaw;

	public Pointer() {
		super("Pointer", "Lets you save temporary points.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			blockPos.add(mc.player.getPosition());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			ChatUtils.message("[Pointer] You can use the command .addPoint <x> <y> <z>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		try {
			blockPos.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		for (BlockPos bb : blockPos) {
			double dd = RotationUtils.getEyesPos().distanceTo(
					Objects.requireNonNull(BlockUtils.getBoundingBox(bb)).getCenter());
			double posXX = bb.getX() + (0) * dd
					- mc.player.posX;
			double posZZ = bb.getZ() + (0) * dd
					- mc.player.posZ;

			theYaw = (int) ((float) Math.toDegrees(Math.atan2(posZZ, posXX)) - 90);
		}

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

			if (!(Math.round(mc.player.rotationYaw) == Math.round(theYaw))) {
				for (BlockPos bbbb : blockPos) {
					int radius = 100;
					for (int y = (int) -radius; y <= radius; y++) {
						GL11.glColor4f(0, 1, 0, 1F);
						GL11.glBegin(GL11.GL_QUADS);
						RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(new BlockPos(bbbb.getX(), bbbb.getY() + y, bbbb.getZ()))));
						GL11.glEnd();
					}
				}
			} else if (Math.round(mc.player.rotationYaw) == Math.round(theYaw)) {
				for (BlockPos bbbb : blockPos) {
					int radius = 100;
					for (int y = (int) -radius; y <= radius; y++) {
						GL11.glColor4f(0, 1, 0, 1F);
						GL11.glBegin(GL11.GL_QUADS);
						RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(new BlockPos(bbbb.getX(), bbbb.getY() + y, bbbb.getZ()))));
						GL11.glEnd();
					}
				}
			}

			GL11.glPopMatrix();

			// GL resets
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);


			Vec3d start = RotationUtils.getClientLookVec()
					.addVector(0, 0, 0)
					.addVector(TileEntityRendererDispatcher.staticPlayerX,
							TileEntityRendererDispatcher.staticPlayerY,
							TileEntityRendererDispatcher.staticPlayerZ);
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

			if (!(Math.round(mc.player.rotationYaw) == Math.round(theYaw))) {
				for (BlockPos bbb : blockPos) {
					GL11.glColor4f(1, 0, 0, 0.8F);
					GL11.glBegin(GL11.GL_LINES);
					RenderUtils.drawArrow(start, new Vec3d(bbb));
					GL11.glEnd();
				}
			} else if (Math.round(mc.player.rotationYaw) == Math.round(theYaw)) {
				for (BlockPos bbb : blockPos) {
					GL11.glColor4f(0, 1, 0, 0.8F);
					GL11.glBegin(GL11.GL_LINES);
					RenderUtils.drawArrow(start, new Vec3d(bbb));
					GL11.glEnd();
				}
			}

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