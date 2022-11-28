/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.block.BlockDirt;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public final class TestGame extends Hack {
	public static float xCoord;
	public static float yCoord;

	public static double score;

	public TestGame() {
		super("BoatRam", "Ram your boat as fast as possible!.");
		setCategory(Category.GAMES);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		xCoord = 3;
		yCoord = 4;

		score = 0;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		score = 0;
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			yCoord = (float) (yCoord - 0.5);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			yCoord = (float) (yCoord + 0.5);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			xCoord = (float) (xCoord + 0.5);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			xCoord = (float) (xCoord - 0.5);
		}



		GL11.glPushMatrix();
		GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
		WMinecraft.getFontRenderer().drawStringWithShadow(String.valueOf(score), 40, 40, (int) 0xFF0000);
		GL11.glPopMatrix();

		ItemStack itemStack = new ItemStack(Items.JUNGLE_BOAT);
		ItemStack crystalStack = new ItemStack(Items.END_CRYSTAL);
		mc.getRenderItem().renderItemIntoGUI(itemStack, (int)xCoord, (int)yCoord);
	}
	public static int random_int(int Min, int Max)
	{
		return (int) (Math.random()*(Max-Min))+Min;
	}
}