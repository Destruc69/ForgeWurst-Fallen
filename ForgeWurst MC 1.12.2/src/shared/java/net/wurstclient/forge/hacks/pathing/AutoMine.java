/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class AutoMine extends Hack {

	private boolean isStarted;

	private BlockPos posA;
	private BlockPos posB;

	private ArrayList<BlockPos> blockPosArrayList;

	public AutoMine() {
		super("AutoMine", "Automatically mines blocks.");
		setCategory(Category.PATHING);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		isStarted = false;
		blockPosArrayList = new ArrayList<>();
		posA = new BlockPos(0, 0, 0);
		posB = new BlockPos(0, 0, 0);

		try {
			ChatUtils.message("Module not done yet.");
			this.setEnabled(false);
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		BlockPos blockPos = mc.objectMouseOver.getBlockPos();
		if (!isStarted) {
			if (posA.getY() == 0 && posB.getY() == 0) {
				if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
					posA = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}
				mc.ingameGUI.setOverlayMessage("Press INSERT to select Pos A", true);
			} else if (!(posA.getY() == 0) && posB.getY() == 0){
				if (Keyboard.isKeyDown(Keyboard.KEY_INSERT)) {
					posB = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}
				mc.ingameGUI.setOverlayMessage("Press INSERT to select Pos B", true);
			} else if (!(posA.getY() == 0) && !(posB.getY() == 0)) {
				blockPosArrayList = getAllBlocksBetween(posA, posB);
				ChatUtils.message("Okay, Engaging!");
				isStarted = true;
			}
		} else {

		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (isStarted) {
			for (BlockPos blockPos : blockPosArrayList) {
			}
		}
	}

	public static ArrayList<BlockPos> getAllBlocksBetween(BlockPos posA, BlockPos posB) {
		ArrayList<BlockPos> blockPosList = new ArrayList<>();

		int minX = Math.min(posA.getX(), posB.getX());
		int minY = Math.min(posA.getY(), posB.getY());
		int minZ = Math.min(posA.getZ(), posB.getZ());
		int maxX = Math.max(posA.getX(), posB.getX());
		int maxY = Math.max(posA.getY(), posB.getY());
		int maxZ = Math.max(posA.getZ(), posB.getZ());

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					blockPosList.add(new BlockPos(x, y, z));
				}
			}
		}

		return blockPosList;
	}
}