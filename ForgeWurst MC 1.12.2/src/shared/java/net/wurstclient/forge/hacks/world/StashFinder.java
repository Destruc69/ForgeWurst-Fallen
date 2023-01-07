/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.util.ArrayList;

public final class StashFinder extends Hack {

	public static ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();
	public static ArrayList<BlockPos> targBlocks = new ArrayList<>();

	private final SliderSetting radius =
			new SliderSetting("Radius", "Radius required.", 20, 10, 40, 1, SliderSetting.ValueDisplay.DECIMAL);

	public StashFinder() {
		super("StashFinder", "Logs chunks with chests/shulkers/enderchests.");
		setCategory(Category.WORLD);
		addSetting(radius);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		blockPosArrayList.clear();
		targBlocks.clear();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		blockPosArrayList.clear();
		targBlocks.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.ticksExisted % 20 != 0) {
			for (int x = -radius.getValueI(); x < radius.getValueF(); x++) {
				for (int z = -radius.getValueI(); z < radius.getValueF(); z++) {
					BlockPos blockPos = new BlockPos(mc.player.getPosition().add(x, 0, z));
					if (!blockPosArrayList.contains(blockPos)) {
						blockPosArrayList.add(blockPos);
					}
				}
			}
		} else {
			blockPosArrayList.clear();
		}
		for (BlockPos blockPos : blockPosArrayList) {
			IBlockState iBlockState = mc.world.getBlockState(blockPos);
			boolean shulkers = iBlockState.getBlock() instanceof BlockShulkerBox;
			boolean chest = iBlockState.getBlock() instanceof BlockChest;
			boolean enderchests = iBlockState.getBlock() instanceof BlockEnderChest;
			if (shulkers || chest || enderchests) {
				targBlocks.add(blockPos);
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		for (BlockPos blockPos : targBlocks) {
			FallenRenderUtils.renderPosFilled(blockPos, event.getPartialTicks(), 1, 0, 0, 0.2f);
		}
	}
}