/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.FallenRenderUtils;

import java.awt.*;
import java.util.ArrayList;

public final class NewChunks extends Hack {

	private static ArrayList<Chunk> chunkArrayList;

	public NewChunks() {
		super("NewChunks", "Shows if chunk has never been loaded before.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		chunkArrayList = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		chunkArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (Chunk chunk : chunkArrayList) {
				double dist = mc.player.getDistance(chunk.x, mc.player.posY, chunk.z);
				if (dist > mc.gameSettings.renderDistanceChunks * 16) {
					chunkArrayList.remove(chunk);
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketChunkData) {
				SPacketChunkData sPacketChunkData = (SPacketChunkData) event.getPacket();
				if (!sPacketChunkData.isFullChunk()) {
					Chunk chunk = new Chunk(mc.world, sPacketChunkData.getChunkX() * 16, sPacketChunkData.getChunkZ() * 16);
					if (!chunkArrayList.contains(chunk)) {
						chunkArrayList.add(chunk);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketChunkData) {
				SPacketChunkData sPacketChunkData = (SPacketChunkData) event.getPacket();
				if (!sPacketChunkData.isFullChunk()) {
					Chunk chunk = new Chunk(mc.world, sPacketChunkData.getChunkX() * 16, sPacketChunkData.getChunkZ() * 16);
					if (!chunkArrayList.contains(chunk)) {
						chunkArrayList.add(chunk);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
			for (Chunk chunk : chunkArrayList) {
				for (int x = -chunk.x; x < chunk.x; x++) {
					for (int z = -chunk.z; z < chunk.z; z++) {
						for (int a = -2; a < 2; a++) {
							FallenRenderUtils.drawLine(new Vec3d(x, 0, z), new Vec3d(x + a, 1, z + a), 5, Color.GREEN);
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
	}
}