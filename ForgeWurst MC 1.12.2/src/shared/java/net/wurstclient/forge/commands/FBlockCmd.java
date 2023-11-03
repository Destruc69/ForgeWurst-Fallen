/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.hacks.pathing.AutoPilot;
import net.wurstclient.forge.hacks.render.Pointer;
import net.wurstclient.forge.pathfinding.PathfinderAStar;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayDeque;
import java.util.Queue;

public final class FBlockCmd extends Command {
	public FBlockCmd() {
		super("fblock", "Finds the nearest coordinate of a specific block.", "Syntax: .fblock <block> <sync-with-autopilot> <sync-with-pointer>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length < 2) {
			throw new CmdSyntaxError("Usage: .fblock <block> <sync-with-autopilot>");
		}

		Block block = Block.getBlockFromName(String.valueOf(args[0]));

		boolean shouldSyncWithAutoPilot = Boolean.parseBoolean(String.valueOf(args[1]));
		boolean shouldSyncWithPointer = Boolean.parseBoolean(String.valueOf(args[2]));

		BlockPos pos = shouldSyncWithAutoPilot ? PathfinderAStar.findNearestReachableBlock(block) : findNearestReachableBlock(block);

		if (pos != null) {
			ChatUtils.message(Math.round(pos.getX()) + " " + Math.round(pos.getY()) + " " + Math.round(pos.getZ()));

			if (shouldSyncWithAutoPilot) {
				AutoPilot.x.setValue(pos.getX());
				AutoPilot.y.setValue(pos.getY());  // Fixed the z value to y
				AutoPilot.z.setValue(pos.getZ());

				ChatUtils.message("Ok, we have synced with AutoPilot module.");
			}
			if (shouldSyncWithPointer) {
				Pointer.x.setValue(pos.getX());
				Pointer.z.setValue(pos.getZ());

				ChatUtils.message("Ok, we have synced with Pointer module.");
			}
		} else {
			ChatUtils.error("We couldnt find that block anywhere.");
		}
	}

	private BlockPos findNearestReachableBlock(Block targetBlock) {
		EntityPlayer entityPlayer = mc.player;
		int maxRadius = mc.gameSettings.renderDistanceChunks * 15; // Set the maximum search radius
		BlockPos playerPos = entityPlayer.getPosition();
		Queue<BlockPos> queue = new ArrayDeque<>();
		boolean[][][] visited = new boolean[maxRadius * 2 + 1][256][maxRadius * 2 + 1];

		queue.add(playerPos);

		while (!queue.isEmpty()) {
			BlockPos currentPos = queue.poll();

			if (mc.world.getBlockState(currentPos).getBlock().equals(targetBlock)) {
				return currentPos; // Return the position if a matching block is found and it's reachable.
			}

			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						BlockPos neighborPos = currentPos.add(x, y, z);

						if (Math.abs(neighborPos.getX() - playerPos.getX()) <= maxRadius &&
								neighborPos.getY() >= 0 && neighborPos.getY() <= 255 &&
								Math.abs(neighborPos.getZ() - playerPos.getZ()) <= maxRadius &&
								!visited[neighborPos.getX() - playerPos.getX() + maxRadius][neighborPos.getY()][neighborPos.getZ() - playerPos.getZ() + maxRadius]) {
							queue.add(neighborPos);
							visited[neighborPos.getX() - playerPos.getX() + maxRadius][neighborPos.getY()][neighborPos.getZ() - playerPos.getZ() + maxRadius] = true;
						}
					}
				}
			}
		}

		return null; // Return null if no matching block is found within the search radius.
	}
}