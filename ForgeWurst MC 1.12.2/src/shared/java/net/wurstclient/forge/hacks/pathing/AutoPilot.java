/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.FallenRenderUtils;

import javax.swing.*;
import java.util.ArrayList;

public final class AutoPilot extends Hack {

	public static ArrayList<BlockPos> allBlocks = new ArrayList<>();
	public static ArrayList<BlockPos> goodBlocks = new ArrayList<>();
	public static ArrayList<BlockPos> distancesFromBlocks = new ArrayList<>();

	public AutoPilot() {
		super("AutoPilot", "Simple navigation highways..");
		setCategory(Category.PATHING);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			setEnabled(false);
			ChatUtils.message("Module not done yet.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (int x = -5; x < 5; x ++) {
			for (int y = -5; y < 5; y ++) {
				for (int z = -5; z < 5; z ++) {
					if (!allBlocks.contains(new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z))) {
						allBlocks.add(new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z));
					}
				}
			}
		}
		for (BlockPos blockPos : allBlocks) {
			boolean check1 = !mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR);
			if (check1) {
				goodBlocks.add(blockPos);
			}
		}
		allBlocks.removeIf(blockPos -> mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 5);
		goodBlocks.removeIf(blockPos -> mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 5);
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		for (BlockPos blockPos : goodBlocks) {
			FallenRenderUtils.renderPosOutline(blockPos, event.getPartialTicks(), 0, 0, 1, 0.5f);
		}
	}
}