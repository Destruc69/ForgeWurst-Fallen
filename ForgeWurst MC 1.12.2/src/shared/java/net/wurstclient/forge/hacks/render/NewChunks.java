/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class NewChunks extends Hack {

	private ArrayList<Chunk> chunkArrayList;

	public NewChunks() {
		super("NewChunks", "Renders whether a chunk has been loaded before.");
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
	public void onUpdate(WPacketInputEvent event) {
		if (event.getPacket() instanceof SPacketChunkData) {
			SPacketChunkData sPacketChunkData = (SPacketChunkData) event.getPacket();
			if (!sPacketChunkData.isFullChunk()) {
				Chunk chunk = new Chunk(mc.world, sPacketChunkData.getChunkX() * 16, sPacketChunkData.getChunkZ() * 16);
				if (!chunkArrayList.contains(chunk)) {
					chunkArrayList.add(chunk);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glPushMatrix();
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
				-TileEntityRendererDispatcher.staticPlayerY,
				-TileEntityRendererDispatcher.staticPlayerZ);


		if (chunkArrayList.size() > 0) {
			for (Chunk chunk : chunkArrayList) {
				if (mc.player.getDistance(chunk.x, mc.player.lastTickPosY, chunk.z) < mc.gameSettings.renderDistanceChunks * 15) {
					for (int x = -10; x < 10; x++) {
						for (int z = -10; z < 10; z++) {
							BlockPos blockPos = new BlockPos(chunk.x + x, 0, chunk.z + z);

							GL11.glColor4f(0, 1, 0, 0.5F);
							GL11.glBegin(GL11.GL_QUADS);
							RenderUtils.drawSolidBox(BlockUtils.getBoundingBox(blockPos));
							GL11.glEnd();
						}
					}
				} else {
					chunkArrayList.remove(chunk);
				}
			}
		}

		GL11.glPopMatrix();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
}