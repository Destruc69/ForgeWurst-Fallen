/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
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
import org.lwjgl.opengl.GL11;

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

	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (event.getPacket() instanceof SPacketChunkData) {
			SPacketChunkData sPacketChunkData = (SPacketChunkData) event.getPacket();
			if (!sPacketChunkData.isFullChunk()) {
				Chunk chunk = new Chunk(mc.world, sPacketChunkData.getChunkX(), sPacketChunkData.getChunkZ());
				if (!chunkArrayList.contains(chunk)) {
					chunkArrayList.add(chunk);
				}
			}
		}

		chunkArrayList.removeIf(chunk -> mc.player.getDistance(chunk.x, mc.player.lastTickPosY, chunk.z) > mc.gameSettings.renderDistanceChunks * 16);
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		for (Chunk chunk : chunkArrayList) {
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(10);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(0, 1, 0, 1.0f);
			GL11.glVertex3d(chunk.x, 0, chunk.z);
			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}
}