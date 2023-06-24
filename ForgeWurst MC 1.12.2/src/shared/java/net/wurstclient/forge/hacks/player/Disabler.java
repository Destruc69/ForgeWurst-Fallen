/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

public final class Disabler extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("PacketAccepted", Mode.values(), Mode.YES);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(mode);
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
		if (event.getPacket() instanceof CPacketConfirmTransaction) {
			CPacketConfirmTransaction cPacketConfirmTransaction = (CPacketConfirmTransaction) event.getPacket();
			mc.player.connection.sendPacket(new CPacketConfirmTransaction(cPacketConfirmTransaction.getWindowId(), cPacketConfirmTransaction.getUid(), mode.getSelected().yes));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
			for (int x = 0; x < 2; x ++) {
				mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
			}
			mc.player.setPosition(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ());
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

	private enum Mode {
		YES("Yes", true, false),
		NO("No", false, true);

		private final String name;
		private final boolean yes;
		private final boolean no;

		private Mode(String name, boolean yes, boolean no) {
			this.name = name;
			this.yes = yes;
			this.no = no;
		}

		public String toString() {
			return name;
		}
	}
}