/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.InventoryUtils;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class AutoClutch extends Hack {

	// This is relaly bad, need tyo fix this in the future

	public AutoClutch() {
		super("AutoClutch", "Saves you from falls by placing a block underneath you if possible.");
		setCategory(Category.WORLD);
	}

	private boolean a;

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		a = true;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (int x = -1; x < 1; x ++) {
			for (int z = -1; z < 1; z ++) {
				if (MathUtils.calculateFallDistance(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ) > 4 && mc.player.motionY < 0) {
					BlockPos blockPos = mc.player.getPosition().add(x + 0.5, -1, z + 0.5);
					if (!(canPlaceBlockAtPos(blockPos) == EnumFacing.UP)) {
						float[] rot = RotationUtils.getRotationsBlock(blockPos, canPlaceBlockAtPos(blockPos));
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));

						if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
							if (a) {
								mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, canPlaceBlockAtPos(blockPos), new Vec3d(canPlaceBlockAtPos(blockPos).getDirectionVec().getX(), canPlaceBlockAtPos(blockPos).getDirectionVec().getY(), canPlaceBlockAtPos(blockPos).getDirectionVec().getZ()), EnumHand.MAIN_HAND);
								mc.player.swingArm(EnumHand.MAIN_HAND);
								a = false;
							}
						} else {
							switchToBlock();
						}
					} else {
						a = true;
					}
				}
			}
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
		} else {
			return EnumFacing.UP;
		}
	}

	private void switchToBlock() {
		for (int i = 0; i < 9; i ++) {
			if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
				InventoryUtils.setSlot(i);
				break;
			}
		}
	}
}