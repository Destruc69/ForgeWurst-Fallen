/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BaseFinder extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	public BaseFinder() {
		super("StashFinder", "Logs positions with man-made obstructions.");
		setCategory(Category.WORLD);
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
		/*
		Still working on it....
		for (Chunk chunk : getLoadedChunks()) {
			for (int x = chunk.getPos().getXStart(); x < chunk.getPos().getXEnd(); x++) {
				for (int y = 0; y < 255; y++) {
					for (int z = chunk.getPos().getZStart(); z < chunk.getPos().getZEnd(); z++) {
						BlockPos blockPos = new BlockPos(x, y, z);
						IBlockState blockState = mc.world.getBlockState(blockPos);

						for (Block block : BlockUtils.NON_NATURAL_BLOCKS) {
							if (blockState.getBlock() == block) {
								if (!blockPosArrayList.contains(blockPos)) {
									blockPosArrayList.add(blockPos);
								}
							}
						}
					}
				}
			}
		}

		 */

		blockPosArrayList.removeIf(blockPos -> mc.player.getDistance(blockPos.getX(), mc.player.lastTickPosY, blockPos.getZ()) > mc.gameSettings.renderDistanceChunks * 15);
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (blockPosArrayList != null) {
			if (blockPosArrayList.size() > 0) {
				// GL settings
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glLineWidth(1);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_DEPTH_TEST);

				GL11.glPushMatrix();
				GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
						-TileEntityRendererDispatcher.staticPlayerY,
						-TileEntityRendererDispatcher.staticPlayerZ);

				for (BlockPos blockPos : blockPosArrayList) {
					GL11.glColor4f(0, 1, 0, 1F);
					GL11.glBegin(GL11.GL_QUADS);
					if (blockPos.getY() < mc.player.lastTickPosY) {
						for (int y = blockPos.getY(); y < mc.player.lastTickPosY; y++) {
							RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(blockPos.add(0, y, 0))));
						}
					} else if (blockPos.getY() > mc.player.lastTickPosY) {
						for (int y = (int) mc.player.lastTickPosY; y < blockPos.getY(); y++) {
							RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(blockPos.add(0, y, 0))));
						}
					}
					GL11.glEnd();
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
	}

	private List<Chunk> getLoadedChunks() {
		List<Chunk> loadedChunks = new ArrayList<>();
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.world != null && mc.player != null) {
			int playerChunkX = mc.player.chunkCoordX;
			int playerChunkZ = mc.player.chunkCoordZ;

			// Define the range around the player to check for loaded chunks
			int radius = mc.gameSettings.renderDistanceChunks * 15; // You can adjust this radius as needed

			for (int chunkX = playerChunkX - radius; chunkX <= playerChunkX + radius; chunkX++) {
				for (int chunkZ = playerChunkZ - radius; chunkZ <= playerChunkZ + radius; chunkZ++) {
					loadedChunks.add(new Chunk(mc.world, chunkX, chunkZ));
				}
			}
		}

		return loadedChunks;
	}
}


