/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.InventoryUtils;
import net.wurstclient.forge.utils.RotationUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AutoGrief extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;
	private ArrayList<BlockPos> allTNTPoses;
	private BlockPos nearestTNTPos;
	private BlockPos theTNTPos;

	private BlockPos potentialPos;

	public AutoGrief() {
		super("AutoGrief", "Helps you grief with ease, With a hybrid of TNT and FlintNSteel.");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		blockPosArrayList = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		blockPosArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (nearestTNTPos != null && theTNTPos != null) {
			blockPosArrayList = new ArrayList<>(Arrays.asList(nearestTNTPos, theTNTPos));
		}

		if (nearestTNTPos != BlockUtils.findNearestBlockPos(mc.player.getPosition(), Blocks.TNT)) {
			nearestTNTPos = BlockUtils.findNearestBlockPos(mc.player.getPosition(), Blocks.TNT);
		}

		if (nearestTNTPos != null) {
			BlockPos playerPos = new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);

			// Find all TNT block positions within a certain radius
			List<BlockPos> allTNTPoses = BlockUtils.findAllBlockPos(playerPos, Blocks.TNT, 16);

			// Hexagonal grid spread pattern logic calculation
			int radius = 4; // Adjust the spread radius as needed

			for (int ring = 0; ring <= radius; ring++) {
				for (int i = 0; i < 6; i++) {
					double angle = (Math.PI / 3) * i;
					double xOffset = Math.cos(angle) * ring * 0.75;
					double zOffset = Math.sin(angle) * ring * 0.75;

					BlockPos potentialPos = new BlockPos(playerPos.getX() + xOffset, playerPos.getY(), playerPos.getZ() + zOffset);

					for (int yOffset = -4; yOffset < 4; yOffset ++) {
						potentialPos.add(0, yOffset, 0);
					}

					boolean isValidPos = true;

					// Check the distance from the nearest TNT block
					if (potentialPos.getDistance(nearestTNTPos.getX(), nearestTNTPos.getY(), nearestTNTPos.getZ()) <= radius) {
						isValidPos = false;
					}

					// Check the distance from all existing TNT blocks
					for (BlockPos existingPos : allTNTPoses) {
						if (potentialPos.getDistance(existingPos.getX(), existingPos.getY(), existingPos.getZ()) <= radius) {
							isValidPos = false;
							break; // Stop checking if the position is not valid
						}
					}

					if (!mc.world.getBlockState(potentialPos).getBlock().equals(Blocks.AIR)) {
						isValidPos = false;
					}

					// Check if placing TNT would obstruct player's path
					if (isValidPos && canPlaceBlockAtPos(potentialPos) != null) {
						theTNTPos = potentialPos;
						break; // Stop iteration once a suitable position is found
					}
				}
			}
		}

		if (theTNTPos != null) {
			if (!mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Item.getItemFromBlock(Blocks.TNT))) {
				int slot = InventoryUtils.getSlot(Item.getItemFromBlock(Blocks.TNT));

				if (mc.player.inventory.currentItem != slot) {
					mc.player.inventory.currentItem = slot;
					mc.playerController.updateController();
				}
			} else {
				if (!mc.world.getBlockState(theTNTPos).getBlock().equals(Blocks.TNT)) {
					if (mc.player.ticksExisted % 10 == 0) {
						mc.playerController.processRightClickBlock(mc.player, mc.world, theTNTPos, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
						mc.player.swingArm(EnumHand.MAIN_HAND);

						float[] rot = RotationUtils.getNeededRotations(new Vec3d(theTNTPos.getX() + 0.5, theTNTPos.getY(), theTNTPos.getZ() + 0.5));
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		for (BlockPos blockPos : blockPosArrayList) {
			assert blockPos != null;
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;

			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(2);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(0, 1, 0, 1f);

			double startX = blockPos.getX() - player.posX;
			double startY = blockPos.getY() - player.posY;
			double startZ = blockPos.getZ() - player.posZ;
			double endX = startX + 1.0;
			double endY = startY + 1.0;
			double endZ = startZ + 1.0;

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(endX, startY, startZ);

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(startX, startY, endZ);

			GL11.glVertex3d(endX, startY, startZ);
			GL11.glVertex3d(endX, startY, endZ);

			GL11.glVertex3d(startX, startY, endZ);
			GL11.glVertex3d(endX, startY, endZ);

			GL11.glVertex3d(startX, endY, startZ);
			GL11.glVertex3d(endX, endY, startZ);

			GL11.glVertex3d(startX, endY, startZ);
			GL11.glVertex3d(startX, endY, endZ);

			GL11.glVertex3d(endX, endY, startZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glVertex3d(startX, endY, endZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(startX, endY, startZ);

			GL11.glVertex3d(startX, startY, endZ);
			GL11.glVertex3d(startX, endY, endZ);

			GL11.glVertex3d(endX, startY, startZ);
			GL11.glVertex3d(endX, endY, startZ);

			GL11.glVertex3d(endX, startY, endZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

	private EnumFacing canPlaceBlockAtPos(BlockPos blockPos) {
		if (!mc.world.getBlockState(blockPos.offset(EnumFacing.NORTH)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.NORTH;
		} else if (!mc.world.getBlockState(blockPos.offset(EnumFacing.EAST)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.EAST;
		} else if (!mc.world.getBlockState(blockPos.offset(EnumFacing.SOUTH)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.SOUTH;
		} else if (!mc.world.getBlockState(blockPos.offset(EnumFacing.WEST)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.WEST;
		} else if (!mc.world.getBlockState(blockPos.offset(EnumFacing.UP)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.UP;
		} else if (!mc.world.getBlockState(blockPos.offset(EnumFacing.DOWN)).getBlock().equals(Blocks.AIR)) {
			return EnumFacing.DOWN;
		} else  {
			return null;
		}
	}
}