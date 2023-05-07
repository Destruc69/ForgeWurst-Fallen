/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class HoleESP extends Hack {

	private static ArrayList<BlockPos> blockPosArrayList;

	private final SliderSetting red = new SliderSetting("red",
			"red", 16, 0, 255, 1, SliderSetting.ValueDisplay.INTEGER);
	private final SliderSetting green = new SliderSetting("green",
			"green", 16, 0, 255, 1, SliderSetting.ValueDisplay.INTEGER);
	private final SliderSetting blue = new SliderSetting("blue",
			"blue", 16, 0, 255, 1, SliderSetting.ValueDisplay.INTEGER);

	public HoleESP() {
		super("HoleESP", "Renders holes.");
		setCategory(Category.RENDER);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
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
	public void onRender(RenderWorldLastEvent event) {
		for (BlockPos pos : blockPosArrayList) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;

			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(2);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(red.getValueF(), green.getValueF(), blue.getValueF(), 1f);

			double startX = pos.getX() - player.posX;
			double startY = pos.getY() - player.posY;
			double startZ = pos.getZ() - player.posZ;
			double endX = startX + 1.0;
			double endY = startY + 1.0;
			double endZ = startZ + 1.0;

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(endX, startY, startZ);

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(startX, startY, endZ);

			GL11.glVertex3d(endX, startY, startZ);
			GL11.glVertex3d(endX, startY, endZ);

			GL11.glVertex3d(startX, startY, endZ);
			GL11.glVertex3d(endX, startY, endZ);

			GL11.glVertex3d(startX, endY, startZ);
			GL11.glVertex3d(endX, endY, startZ);

			GL11.glVertex3d(startX, endY, startZ);
			GL11.glVertex3d(startX, endY, endZ);

			GL11.glVertex3d(endX, endY, startZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glVertex3d(startX, endY, endZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glVertex3d(startX, startY, startZ);
			GL11.glVertex3d(startX, endY, startZ);

			GL11.glVertex3d(startX, startY, endZ);
			GL11.glVertex3d(startX, endY, endZ);

			GL11.glVertex3d(endX, startY, startZ);
			GL11.glVertex3d(endX, endY, startZ);

			GL11.glVertex3d(endX, startY, endZ);
			GL11.glVertex3d(endX, endY, endZ);

			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (int x = -20; x < 20; x ++) {
			for (int y = -20; y < 20; y ++) {
				for (int z = -20; z < 20; z ++) {
					BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
					if (isHole(blockPos)) {
						if (!blockPosArrayList.contains(blockPos)) {
							blockPosArrayList.add(blockPos);
						}
					}
				}
			}
		}
		blockPosArrayList.removeIf(blockPos -> !isHole(blockPos));
	}

	private boolean isHole(BlockPos blockPos) {
		IBlockState state = mc.world.getBlockState(blockPos);
		return state.getBlock().equals(Blocks.AIR) &&
				!mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) &&
				!mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) &&
				!mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) &&
				!mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR) &&
				!mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock().equals(Blocks.AIR);
	}
}