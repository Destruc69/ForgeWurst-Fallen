/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.wurstclient.forge.Hack;

import static scala.util.control.TailCalls.done;

public final class BlockUtil extends Hack {


	public BlockUtil(String name, String description) {
		super(name, description);
	}

	public static BlockPos findBlock(Block block, int radius) {
		for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
			for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
				for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
					BlockPos pos = new BlockPos(x, y, z);
					if (mc.world.getBlockState(pos).getBlock().equals(block)) {
						return pos;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets all the BlockPositions in the given radius around the player
	 */
	public static List<BlockPos> getAll(int radius) {
		List<BlockPos> list = new ArrayList<BlockPos>();
		try {
			for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
				for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
					for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
						list.add(new BlockPos(x, y, z));
					}
				}
			}

			list.sort(new Comparator<BlockPos>() {
				@Override
				public int compare(BlockPos lhs, BlockPos rhs) {
					return Double.compare(mc.player.getDistanceSq(lhs), mc.player.getDistanceSq(rhs));
				}
			});

			return list;
		} catch (Exception e) {
			return list;
		}
	}

	public static ArrayList<BlockPos> getAllNoSort(int radius) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();

		for (int x = (int) (mc.player.posX - radius); x < mc.player.posX + radius; x++) {
			for (int z = (int) (mc.player.posZ - radius); z < mc.player.posZ + radius; z++) {
				for (int y = (int) (mc.player.posY + radius); y > mc.player.posY - radius; y--) {
					list.add(new BlockPos(x, y, z));
				}
			}
		}

		return list;
	}

	/**
	 * Gets all the BlockPositions in the given radius around the pos
	 */
	public static ArrayList<BlockPos> getAll(Vec3d pos, int radius) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();

		for (int x = (int) (pos.x - radius); x < pos.x + radius; x++) {
			for (int z = (int) (pos.z - radius); z < pos.z + radius; z++) {
				for (int y = (int) (pos.y + radius); y > pos.y - radius; y--) {
					list.add(new BlockPos(x, y, z));
				}
			}
		}

		return list;
	}

	/**
	 * Checks if a block can be placed to this position
	 */
	/**
	 * Places a block to the given BlockPosition
	 * This is run on the client thread
	 *
	 * @pos Places the block to this position
	 * @block The block to place. Must be in ur inventory!
	 */

	public static void placeBlockNoSleep(Block block, BlockPos pos, boolean spoofRotation) {
		new Place(null, block, pos, spoofRotation);
	}
	/**
	 * Same as the placeBlock but it interacts with the given block with the given item
	 * This is run on the client thread
	 */

	public static void placeItemNoSleep(Item item, BlockPos pos, boolean spoofRotation) {
		new Place(item, null, pos, spoofRotation);
	}


	/**
	 * Distance between these 2 blockpositions
	 */
	public static int distance(BlockPos first, BlockPos second) {
		return Math.abs(first.getX() - second.getX()) + Math.abs(first.getY() - second.getY()) + Math.abs(first.getZ() - second.getZ());
	}

    public static boolean isValidBlock(BlockPos pos) {
		Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
		return !(block instanceof BlockLiquid) && block.getMaterial(null) != Material.AIR;
    }

    /**
	 * Checks if the block is in render distance or known by the client.
	 */

	/**
	 * Checks if any neighbor block can be right clicked
	 */

	/**
	 * Checks if the pos can be seen
	 */
	public static class Place {
		public boolean done, success, spoofRotation, dontRotate, rotateSpoofNoPacket, dontStopRotating;
		public Item item;
		public Block block;
		public BlockPos pos;
		public EnumFacing setFacing;

		public Place(Item item, Block block, BlockPos pos, boolean spoofRotation) {
			this.item = item;
			this.pos = pos;
			this.block = block;
			this.spoofRotation = spoofRotation;
			MinecraftForge.EVENT_BUS.register(this);
		}
	}
}