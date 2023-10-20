/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.PathfinderAStar;

import java.util.ArrayList;

public final class Breadcrumbs extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	public Breadcrumbs() {
		super("Breadcrumbs", "Trace back your path with rendering.");
		setCategory(Category.RENDER);
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
		BlockPos blockPos = new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 1, mc.player.lastTickPosZ);
		if (!blockPosArrayList.contains(blockPos)) {
			blockPosArrayList.add(blockPos);
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (blockPosArrayList.size() > 2) {
			// Just use pathfinding renders
			PathfinderAStar.render(blockPosArrayList, 1, 0, 1, 0);
		}
	}
}