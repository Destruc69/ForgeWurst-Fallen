/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class BaseFinder extends Hack {

	private Map<BlockPos, Integer> linkCountMap;
	private Map<BlockPos, Double> distanceToClosestMap;

	public BaseFinder() {
		super("StashFinder", "Logs positions with man-made obstructions.");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		linkCountMap = new HashMap<>();
		distanceToClosestMap = new HashMap<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (mc.player.ticksExisted % 20 == 0) {
			ArrayList<BlockPos> blockPosArrayList = BlockUtils.getManMadeObstructions(BlockUtils.getBlockPosWithinRenderDistance(mc.player));

			blockPosArrayList.sort(new Comparator<BlockPos>() {
				@Override
				public int compare(BlockPos pos1, BlockPos pos2) {
					// Compare by distance to closest first
					double distanceDiff = distanceToClosestMap.get(pos1) - distanceToClosestMap.get(pos2);
					if (distanceDiff != 0) {
						return Double.compare(distanceDiff, 0);
					}

					// Compare by link count
					int linkCountDiff = linkCountMap.get(pos1) - linkCountMap.get(pos2);
					if (linkCountDiff != 0) {
						return Integer.compare(linkCountDiff, 0);
					}

					// If distances and link counts are the same, maintain original order
					return 0;
				}
			});


		}
	}
}


