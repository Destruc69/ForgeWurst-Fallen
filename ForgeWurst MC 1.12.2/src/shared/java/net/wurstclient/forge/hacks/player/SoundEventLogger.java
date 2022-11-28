/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class SoundEventLogger extends Hack {

	ArrayList<BlockPos> openChunks = new ArrayList<>();

	public SoundEventLogger() {
		super("SoundEventLogger", "If we hear sound like ligtning, And is not you. it logs the pos as there is a player there. \n" +
				"And other ways too");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		openChunks.clear();
	}

	@SubscribeEvent
	public void onPacketInput(WPacketInputEvent event) {
		try {
			for (int x = -30; x < 30; x++) {
				for (int z = -30; z < 30; z++) {
					if (event.getPacket() instanceof SPacketSoundEffect) {
						SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect) event.getPacket();
						if (!(sPacketSoundEffect.getX() == mc.player.posX + x) && !(sPacketSoundEffect.getZ() == mc.player.posZ + z)) {
							for (int y = -20; y < 20; y++) {
								openChunks.add(new BlockPos(sPacketSoundEffect.getX(), sPacketSoundEffect.getY() + y, sPacketSoundEffect.getZ()));
								ChatUtils.message("[SELOGGER] SPacketSoundEffect was recieved at" + " " + Math.round(sPacketSoundEffect.getX()) + " " + Math.round(sPacketSoundEffect.getY()) + " " + Math.round(sPacketSoundEffect.getZ()));
							}
						}
					}
					if (event.getPacket() instanceof SPacketSpawnPlayer) {
						SPacketSpawnPlayer sPacketSpawnPlayer = (SPacketSpawnPlayer) event.getPacket();
						if (!(sPacketSpawnPlayer.getEntityID() == mc.player.getEntityId())) {
							if (!(sPacketSpawnPlayer.getX() == mc.player.posX + x) && !(sPacketSpawnPlayer.getZ() == mc.player.posZ + z)) {
								for (int y = -20; y < 20; y++) {
									openChunks.add(new BlockPos(sPacketSpawnPlayer.getX(), sPacketSpawnPlayer.getY() + y, sPacketSpawnPlayer.getZ()));
									ChatUtils.message("[SELOGGET] SPacketSpawnPlayer was recieved at" + " " + Math.round(sPacketSpawnPlayer.getX()) + " " + Math.round(sPacketSpawnPlayer.getY()) + " " + Math.round(sPacketSpawnPlayer.getZ()));
								}
							}
						}
					}
					if (event.getPacket() instanceof SPacketEffect) {
						SPacketEffect sPacketEffect = (SPacketEffect) event.getPacket();
						if (!(sPacketEffect.getSoundPos().getX() == mc.player.posX + x) && !(sPacketEffect.getSoundPos().getZ() == mc.player.posZ + z)) {
							for (int y = -20; y < 20; y++) {
								openChunks.add(sPacketEffect.getSoundPos().add(0, y, 0));
								ChatUtils.message("[SELOGGER] SPacketEffect was recieved at" + " " + Math.round(sPacketEffect.getSoundPos().getX()) + " " + Math.round(sPacketEffect.getSoundPos().getY()) + " " + Math.round(sPacketEffect.getSoundPos().getZ()));
							}
						}
					}
					if (event.getPacket() instanceof SPacketSpawnMob) {
						SPacketSpawnMob sPacketSpawnMob = (SPacketSpawnMob) event.getPacket();
						if (!(sPacketSpawnMob.getX() == mc.player.posX + x) && !(sPacketSpawnMob.getZ() == mc.player.posZ + z)) {
							for (int y = -20; y < 20; y++) {
								openChunks.add(new BlockPos(sPacketSpawnMob.getX(), sPacketSpawnMob.getY() + y, sPacketSpawnMob.getZ()));
								ChatUtils.message("[SELOGGER] SPacketSpawnMob was recieved at" + " " + Math.round(sPacketSpawnMob.getX()) + " " + Math.round(sPacketSpawnMob.getY()) + " " + Math.round(sPacketSpawnMob.getZ()));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		try {
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

			for (BlockPos posPos : openChunks) {
				assert posPos != null;
				GL11.glColor4f(0, 1, 0, 1F);
				GL11.glBegin(GL11.GL_QUADS);
				RenderUtils.drawSolidBox(Objects.requireNonNull(BlockUtils.getBoundingBox(posPos)));
				GL11.glEnd();
			}

			GL11.glPopMatrix();

			// GL resets
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}