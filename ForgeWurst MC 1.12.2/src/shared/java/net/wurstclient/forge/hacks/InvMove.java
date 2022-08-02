/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPlayerMoveEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;

public final class InvMove extends Hack {

	private final CheckboxSetting spoof =
			new CheckboxSetting("Spoof", "Sends close inv packet when client inv is open (tank u cookie client my beloved)",
					false);

	public InvMove() {
		super("InvMove", "Allows you to move with your inventory (tank u cookie client my beloved).");
		setCategory(Category.MOVEMENT);
		addSetting(spoof);
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
			if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiEditSign || mc.currentScreen instanceof GuiScreenBook || mc.currentScreen instanceof GuiRepair) {
				return;
			}

			mc.player.movementInput.moveStrafe = 0.0F;
			mc.player.movementInput.moveForward = 0.0F;

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode())) {
				mc.player.setSprinting(true);
			}

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
				++mc.player.movementInput.moveForward;
				mc.player.movementInput.forwardKeyDown = true;
			} else {
				mc.player.movementInput.forwardKeyDown = false;
			}

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
				--mc.player.movementInput.moveForward;
				mc.player.movementInput.backKeyDown = true;
			} else {
				mc.player.movementInput.backKeyDown = false;
			}

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
				++mc.player.movementInput.moveStrafe;
				mc.player.movementInput.leftKeyDown = true;
			} else {
				mc.player.movementInput.leftKeyDown = false;
			}

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
				--mc.player.movementInput.moveStrafe;
				mc.player.movementInput.rightKeyDown = true;
			} else {
				mc.player.movementInput.rightKeyDown = false;
			}

			KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
			mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		try {
			if (!spoof.isChecked())
				return;

			if (event.getPacket() instanceof CPacketEntityAction) {
				CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
				if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}