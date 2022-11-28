/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.FMLSecurityManager;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.TextAreaOutputStream;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.TextUtil;
import org.lwjgl.input.Mouse;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;

public final class NoCom extends Hack {
	public static BlockPos node1;
	public static BlockPos node2;
	public static BlockPos node3;
	public static BlockPos node4;
	public static BlockPos node5;

	public NoCom() {
		super("NoCom", "A NoCom clone. \n" +
				"ONLY WORKS ON OLD VERSIONS OF SERVERS");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			ChatUtils.warning("Firstly, This is as simple as it gets. We break a random block every few ticks to load the chunk\n" +
					"Server will only respond if the chunk is loaded by a player is if we find a responsive block then it means\n" +
					"the chunk is loaded. \n" +
					TextUtil.coloredString("This is a very computer intensive module", TextUtil.Color.RED) + "\n" +
					TextUtil.coloredString("If nothing is happening its because were still searching through blocks, Please be very patient, And save chat logs", TextUtil.Color.RED));
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
		try {
			if (mc.player.ticksExisted % 1.1 == 0) {
				node1 = new BlockPos(mc.player.posX + Math.random() * 99999999 - Math.random() * 99999999, mc.player.posY, mc.player.posZ + Math.random() * 99999999 - Math.random() * 99999999);
				node2 = new BlockPos(mc.player.posX + Math.random() * 9999999 - Math.random() * 99999999, mc.player.posY, mc.player.posZ + Math.random() * 99999999 - Math.random() * 99999999);
				node3 = new BlockPos(mc.player.posX + Math.random() * 99999999 - Math.random() * 99999999, mc.player.posY, mc.player.posZ + Math.random() * 99999999 - Math.random() * 99999999);
				node4 = new BlockPos(mc.player.posX + Math.random() * 99999999 - Math.random() * 99999999, mc.player.posY, mc.player.posZ + Math.random() * 99999999 - Math.random() * 99999999);
				node5 = new BlockPos(mc.player.posX + Math.random() * 99999999 - Math.random() * 99999999, mc.player.posY, mc.player.posZ + Math.random() * 99999999 - Math.random() * 99999999);
			}
			for (int xxxx = 0; xxxx < 2; xxxx++) {
				mc.playerController.onPlayerDamageBlock(node1, EnumFacing.DOWN);
				mc.playerController.onPlayerDamageBlock(node2, EnumFacing.DOWN);
				mc.playerController.onPlayerDamageBlock(node3, EnumFacing.DOWN);
				mc.playerController.onPlayerDamageBlock(node4, EnumFacing.DOWN);
				mc.playerController.onPlayerDamageBlock(node5, EnumFacing.DOWN);
				mc.playerController.clickBlock(node1, EnumFacing.DOWN);
				mc.playerController.clickBlock(node2, EnumFacing.DOWN);
				mc.playerController.clickBlock(node3, EnumFacing.DOWN);
				mc.playerController.clickBlock(node4, EnumFacing.DOWN);
				mc.playerController.clickBlock(node5, EnumFacing.DOWN);
				mc.playerController.onPlayerDestroyBlock(node1);
				mc.playerController.onPlayerDestroyBlock(node2);
				mc.playerController.onPlayerDestroyBlock(node3);
				mc.playerController.onPlayerDestroyBlock(node4);
				mc.playerController.onPlayerDestroyBlock(node5);
			}
			mc.player.swingArm(EnumHand.MAIN_HAND);

			Chunk chunk1 = new Chunk(mc.world, node1.getX(), node1.getZ());
			Chunk chunk2 = new Chunk(mc.world, node2.getX(), node2.getZ());
			Chunk chunk3 = new Chunk(mc.world, node3.getX(), node3.getZ());
			Chunk chunk4 = new Chunk(mc.world, node4.getX(), node4.getZ());
			Chunk chunk5 = new Chunk(mc.world, node5.getX(), node5.getZ());

			if (!mc.world.getBlockState(node1).getBlock().equals(Blocks.AIR) || chunk1.isLoaded()) {
				ChatUtils.message("[NC] Block was responsive thus chunk is loaded at:" + " " + Math.round(node1.getX()) + " " + Math.round(node1.getY()) + " " + Math.round(node1.getZ()));
			}
			if (!mc.world.getBlockState(node2).getBlock().equals(Blocks.AIR) || chunk2.isLoaded()) {
				ChatUtils.message("[NC] Block was responsive thus chunk is loaded at:" + " " + Math.round(node2.getX()) + " " + Math.round(node2.getY()) + " " + Math.round(node2.getZ()));
			}
			if (!mc.world.getBlockState(node3).getBlock().equals(Blocks.AIR) || chunk3.isLoaded()) {
				ChatUtils.message("[NC] Block was responsive thus chunk is loaded at:" + " " + Math.round(node3.getX()) + " " + Math.round(node3.getY()) + " " + Math.round(node3.getZ()));
			}
			if (!mc.world.getBlockState(node4).getBlock().equals(Blocks.AIR) || chunk4.isLoaded()) {
				ChatUtils.message("[NC] Block was responsive thus chunk is loaded at:" + " " + Math.round(node4.getX()) + " " + Math.round(node4.getY()) + " " + Math.round(node4.getZ()));
			}
			if (!mc.world.getBlockState(node5).getBlock().equals(Blocks.AIR) || chunk5.isLoaded()) {
				ChatUtils.message("[NC] Block was responsive thus chunk is loaded at:" + " " + Math.round(node5.getX()) + " " + Math.round(node5.getY()) + " " + Math.round(node5.getZ()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}