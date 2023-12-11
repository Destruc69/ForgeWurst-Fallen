/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
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
		if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiEditSign) && mc.currentScreen != null) {
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
				event.getMovementInput().moveForward = 1;
				event.getMovementInput().forwardKeyDown = true;
			}
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
				event.getMovementInput().moveForward = -1;
				event.getMovementInput().backKeyDown = true;
			}
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
				event.getMovementInput().moveStrafe = -1;
				event.getMovementInput().rightKeyDown = true;
			}
			if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
				event.getMovementInput().moveStrafe = 1;
				event.getMovementInput().leftKeyDown = true;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				mc.player.rotationYaw = mc.player.rotationYaw + 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				mc.player.rotationYaw = mc.player.rotationYaw - 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				mc.player.rotationPitch = mc.player.rotationPitch - 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				mc.player.rotationPitch = mc.player.rotationPitch + 3;
			}
			event.getMovementInput().jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
			event.getMovementInput().sneak = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (event.getPacket() instanceof CPacketEntityAction) {
			CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
			if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
				event.setCanceled(true);
			}
		}
	}
}