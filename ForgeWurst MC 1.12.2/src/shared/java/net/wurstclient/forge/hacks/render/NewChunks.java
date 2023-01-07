/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.util.ArrayList;

public final class NewChunks extends Hack {

	public static ArrayList<Chunk> fullChunks = new ArrayList<>();

	public NewChunks() {
		super("NewChunks", "Shows if chunk has never been loaded before.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			ChatUtils.error("Not completed yet");
			setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fullChunks.clear();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		fullChunks.clear();
	}

	@SubscribeEvent
	public void onUpdate(WPacketOutputEvent event) {
		SPacketChunkData sPacketChunkData = (SPacketChunkData) event.getPacket();
		if (sPacketChunkData.isFullChunk()) {
			Chunk chunk = new Chunk(mc.world, sPacketChunkData.getChunkX() * 16, sPacketChunkData.getChunkZ() * 16);
			if (!fullChunks.contains(chunk)) {
				fullChunks.add(chunk);
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
			for (Chunk chunk : fullChunks) {
				FallenRenderUtils.renderPosFilled(new BlockPos(chunk.x, mc.player.posY - mc.player.fallDistance, chunk.z), event.getPartialTicks(), 0, 0, 100, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}