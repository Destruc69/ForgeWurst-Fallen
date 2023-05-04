/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

import java.util.ArrayList;

public final class Disabler extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.CANCEL);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(mode);
	}

	private enum Mode {
		CANCEL("Cancel", true, false, false, false, false, false),
		SPOOF("Spoof", false, true, false, false, false, false),
		FUCKTHESERVER("FUCKTHESERVER", false, false, true, false, false, false),
		ABILITIES("Abilities", false, false, false, true, false, false),
		SPECTATE("Spectate", false, false, false, false, true, false),
		LAG("Lag", false, false, false, false, false, true);

		private final String name;
		private final boolean cancel;
		private final boolean spoof;
		private final boolean fucktheserver;
		private final boolean abilities;
		private final boolean spectate;
		private final boolean lag;

		private Mode(String name, boolean cancel, boolean spoof, boolean fucktheserver, boolean abilities, boolean spectate, boolean  lag) {
			this.name = name;
			this.cancel = cancel;
			this.spoof = spoof;
			this.fucktheserver = fucktheserver;
			this.abilities = abilities;
			this.spectate = spectate;
			this.lag = lag;
		}

		public String toString() {
			return name;
		}
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
		if (mode.getSelected().abilities) {
			PlayerCapabilities playerCapabilities = new PlayerCapabilities();
			playerCapabilities.allowEdit = true;
			playerCapabilities.disableDamage = true;
			playerCapabilities.allowFlying = true;
			playerCapabilities.isCreativeMode = true;
			playerCapabilities.isFlying = false;
			CPacketPlayerAbilities cPacketPlayerAbilities = new CPacketPlayerAbilities(playerCapabilities);
			mc.player.connection.sendPacket(cPacketPlayerAbilities);
		} else if (mode.getSelected().spectate) {
			CPacketSpectate cPacketSpectate = new CPacketSpectate(mc.player.getUniqueID());
			mc.player.connection.sendPacket(cPacketSpectate);
		}
	}

	private static final ArrayList<Packet> savedPackets = new ArrayList<>();

	@SubscribeEvent
	public void inPacketEvent(WPacketInputEvent event) {
		try {
			if (mode.getSelected().cancel) {
				if (event.getPacket() instanceof CPacketKeepAlive || event.getPacket() instanceof CPacketConfirmTransaction) {
					event.setCanceled(true);
				}
			} else if (mode.getSelected().spoof) {
				if (event.getPacket() instanceof CPacketConfirmTransaction) {
					CPacketConfirmTransaction cPacketConfirmTransaction = (CPacketConfirmTransaction) event.getPacket();
					mc.player.connection.sendPacket(new CPacketConfirmTransaction(cPacketConfirmTransaction.getWindowId(), cPacketConfirmTransaction.getUid(), false));
					event.setCanceled(true);
				}
				if (event.getPacket() instanceof CPacketKeepAlive) {
					mc.player.connection.sendPacket(new CPacketKeepAlive((long) (Integer.MIN_VALUE + Math.random() * 1)));
					event.setCanceled(true);
				}
			} else if (mode.getSelected().lag) {
				if (!(mc.player.ticksExisted % 10 == 0)) {
					savedPackets.add(event.getPacket());
					event.setCanceled(true);
				} else {
					mc.player.connection.sendPacket((Packet<?>) savedPackets);
					savedPackets.clear();
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (mode.getSelected().fucktheserver) {
				event.setCanceled(true);
			}
		} catch (Exception ignored) {
		}
	}
}