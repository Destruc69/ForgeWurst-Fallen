/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Objects;

public final class OreFinder extends Hack {
	public static ArrayList<BlockPos> ores = new ArrayList<>();

	public OreFinder() {
		super("OreFinder", "Finds ores for you automaticly.");
		setCategory(Category.PATHING);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (BlockPos theOre : ores) {
				for (int x = -400; x < 400; x++) {
					for (int z = -400; z < 400; z++) {
						for (int y = -400; y < 400; y++) {
							BlockPos blockPos = new BlockPos(mc.player.getPosition().add(x, y, z));
							if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.DIAMOND_ORE) ||
									mc.world.getBlockState(blockPos).getBlock().equals(Blocks.IRON_ORE) ||
									mc.world.getBlockState(blockPos).getBlock().equals(Blocks.GOLD_ORE)) {
								if (theOre != blockPos) {
									ores.add(blockPos);
								}
							}
						}
					}
				}

				if (!(mc.player.posY == theOre.getY())) {
					mc.playerController.onPlayerDamageBlock(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ), EnumFacing.DOWN);
					for (int x = 0; x < 2; x++) {
						mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90, mc.player.onGround));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}