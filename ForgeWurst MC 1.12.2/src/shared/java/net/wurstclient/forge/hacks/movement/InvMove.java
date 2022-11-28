/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import org.lwjgl.input.Keyboard;

public final class InvMove extends Hack {

	public InvMove() {
		super("InvMove", "Allows you to move with your inventory.");
		setCategory(Category.MOVEMENT);
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
	public void onInput(InputUpdateEvent event) {
		assert event != null;
		try {
			if (mc.currentScreen instanceof GuiChat)
				return;
			if (Keyboard.isKeyDown(Keyboard.KEY_W) || mc.gameSettings.keyBindForward.isKeyDown()) {
				event.getMovementInput().moveForward++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S) || mc.gameSettings.keyBindBack.isKeyDown()) {
				event.getMovementInput().moveForward--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D) || mc.gameSettings.keyBindRight.isKeyDown()) {
				event.getMovementInput().moveStrafe--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A) || mc.gameSettings.keyBindLeft.isKeyDown()) {
				event.getMovementInput().moveStrafe++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || mc.gameSettings.keyBindJump.isKeyDown()) {
				event.getMovementInput().jump = true;
			} else {
				event.getMovementInput().jump = false;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || mc.gameSettings.keyBindSneak.isKeyDown()) {
				event.getMovementInput().sneak = true;
			} else {
				event.getMovementInput().sneak = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}