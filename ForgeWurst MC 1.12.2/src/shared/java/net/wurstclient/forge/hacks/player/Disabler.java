/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;

public final class Disabler extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.BASIC);

	private final CheckboxSetting pingSpoof =
			new CheckboxSetting("PingSpoof", "Spoofs your ping.",
					false);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(mode);
		addSetting(pingSpoof);
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
		if (mode.getSelected().extra) {
			if (event.getPacket() instanceof CPacketConfirmTransaction ||
					event.getPacket() instanceof CPacketCustomPayload ||
					event.getPacket() instanceof CPacketEntityAction) {
				event.setCanceled(true);
			}
		} else if (mode.getSelected().basic) {
			if (event.getPacket() instanceof CPacketConfirmTransaction ||
					event.getPacket() instanceof CPacketCustomPayload) {
				event.setCanceled(true);
			}
		}
		if (pingSpoof.isChecked()) {
			if (event.getPacket() instanceof CPacketPing) {
				CPacketPing cPacketPing = new CPacketPing(0L);
				event.setPacket(cPacketPing);
			}
		}
	}

	private enum Mode {
		BASIC("Basic", true, false),
		EXTRA("Extra", false, true),
		NONE("None", false, false);

		private final String name;
		private final boolean basic;
		private final boolean extra;

		private Mode(String name, boolean basic, boolean extra) {
			this.name = name;
			this.basic = basic;
			this.extra = extra;
		}

		public String toString() {
			return name;
		}
	}
}