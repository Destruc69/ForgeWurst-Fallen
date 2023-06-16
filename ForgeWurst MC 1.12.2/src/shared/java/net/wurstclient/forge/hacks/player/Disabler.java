/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class Disabler extends Hack {

	private long theId;

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
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
	public void onPacket(WPacketOutputEvent event) {
		if (event.getPacket() instanceof CPacketConfirmTransaction || event.getPacket() instanceof CPacketCustomPayload || event.getPacket() instanceof CPacketEntityAction) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		double f = mc.player.movementInput.moveForward;
		double s = mc.player.movementInput.moveStrafe;
		if (f > 0 && s > 0) {
			mc.player.connection.sendPacket(new CPacketInput(Integer.MAX_VALUE, Integer.MAX_VALUE, mc.player.movementInput.jump, mc.player.movementInput.sneak));
		} else if (f < 0 && s < 0) {
			mc.player.connection.sendPacket(new CPacketInput(Integer.MIN_VALUE, Integer.MIN_VALUE, mc.player.movementInput.jump, mc.player.movementInput.sneak));
		} else if (f > 0 && s < 0) {
			mc.player.connection.sendPacket(new CPacketInput(Integer.MIN_VALUE, Integer.MAX_VALUE, mc.player.movementInput.jump, mc.player.movementInput.sneak));
		} else if (f < 0 && s > 0) {
			mc.player.connection.sendPacket(new CPacketInput(Integer.MAX_VALUE, Integer.MIN_VALUE, mc.player.movementInput.jump, mc.player.movementInput.sneak));
		} else {
			mc.player.connection.sendPacket(new CPacketInput(0, 0, mc.player.movementInput.jump, mc.player.movementInput.sneak));
		}
	}
}