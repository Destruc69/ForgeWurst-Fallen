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
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import org.lwjgl.input.Keyboard;

public final class InvMove extends Hack {

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Bypass anticheats.",
					false);

	public InvMove() {
		super("InvMove", "Allows you to move with your inventory.");
		setCategory(Category.MOVEMENT);
		addSetting(bypass);
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
			event.getMovementInput().jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE) || mc.gameSettings.keyBindJump.isKeyDown();
			event.getMovementInput().sneak = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || mc.gameSettings.keyBindSneak.isKeyDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (bypass.isChecked()) {
				if (event.getPacket() instanceof CPacketEntityAction) {
					CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
					if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
						event.setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (bypass.isChecked()) {
				if (event.getPacket() instanceof CPacketEntityAction) {
					CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
					if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
						event.setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}