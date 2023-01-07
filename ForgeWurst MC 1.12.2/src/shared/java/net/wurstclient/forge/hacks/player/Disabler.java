/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

public final class Disabler extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.BASIC);

	private enum Mode {
		BASIC("Basic", true, false),
		BASICV2("BASIC V2", false, true);

		private final String name;
		private final boolean basicv2;
		private final boolean basic;

		private Mode(String name, boolean basic, boolean basicv2) {
			this.name = name;
			this.basic = basic;
			this.basicv2 = basicv2;
		}

		public String toString() {
			return name;
		}
	}

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
	public void inPacketEvent(WPacketInputEvent event) {
		if (mode.getSelected().basic) {
			if (event.getPacket() instanceof CPacketKeepAlive) {
				mc.player.connection.sendPacket(new CPacketKeepAlive((long) (Integer.MIN_VALUE + Math.random() * Integer.MIN_VALUE)));
				event.setCanceled(true);
			}
			if (event.getPacket() instanceof CPacketConfirmTransaction) {
				CPacketConfirmTransaction cPacketConfirmTransaction = (CPacketConfirmTransaction) event.getPacket();
				mc.player.connection.sendPacket(new CPacketConfirmTransaction(Integer.MAX_VALUE, cPacketConfirmTransaction.getUid(), false));
				event.setCanceled(true);
			}
		} else {
			try {
				PlayerCapabilities playerCapabilities = new PlayerCapabilities();
				playerCapabilities.isFlying = true;
				playerCapabilities.isCreativeMode = true;
				playerCapabilities.allowFlying = true;
				playerCapabilities.allowEdit = true;
				playerCapabilities.disableDamage = true;
				mc.player.connection.sendPacket(new CPacketPlayerAbilities(playerCapabilities));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@SubscribeEvent
	public void outPacketEvent(WPacketOutputEvent event) {
		if (mode.getSelected().basic) {
			if (event.getPacket() instanceof SPacketConfirmTransaction) {
				SPacketConfirmTransaction sPacketConfirmTransaction = (SPacketConfirmTransaction) event.getPacket();
				if (sPacketConfirmTransaction.getActionNumber() <= 0) {
					event.setCanceled(true);
				}
			}
		}
	}
}