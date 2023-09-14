/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class Disabler extends Hack {

	private final CheckboxSetting pingSpoof =
			new CheckboxSetting("PingSpoof",
					true);

	private final CheckboxSetting confirmPositions =
			new CheckboxSetting("ConfirmPositions",
					true);

	private final CheckboxSetting declineTransactions =
			new CheckboxSetting("DeclineTransactions",
					true);

	private final CheckboxSetting cancelAllCustomPayloads =
			new CheckboxSetting("CancelAllCustomPayloads",
					true);

	private final CheckboxSetting antiFlag =
			new CheckboxSetting("AntiFlag",
					true);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(pingSpoof);
		addSetting(confirmPositions);
		addSetting(declineTransactions);
		addSetting(cancelAllCustomPayloads);
		addSetting(antiFlag);
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
	public void onPacketOut(WPacketOutputEvent event) {
		if (declineTransactions.isChecked()) {
			if (event.getPacket() instanceof CPacketConfirmTransaction) {
				CPacketConfirmTransaction confirmTransaction = (CPacketConfirmTransaction) event.getPacket();
				CPacketConfirmTransaction cPacketConfirmTransaction = new CPacketConfirmTransaction(confirmTransaction.getWindowId(), confirmTransaction.getUid(), false);
				event.setPacket(cPacketConfirmTransaction);
			}
		}
		if (pingSpoof.isChecked()) {
			if (event.getPacket() instanceof CPacketPing) {
				CPacketPing cPacketPing = new CPacketPing(0);
				event.setPacket(cPacketPing);
			}
		}
		if (cancelAllCustomPayloads.isChecked()) {
			if (event.getPacket() instanceof CPacketCustomPayload) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (confirmPositions.isChecked()) {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
			}
		}
		if (pingSpoof.isChecked()) {
			if (event.getPacket() instanceof SPacketKeepAlive) {
				SPacketKeepAlive sPacketKeepAlive = (SPacketKeepAlive) event.getPacket();
				mc.player.connection.sendPacket(new CPacketKeepAlive(sPacketKeepAlive.getId() + Minecraft.getDebugFPS() / 2));
				event.setCanceled(true);
			}
		}
		if (antiFlag.isChecked()) {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				event.setPacket(new SPacketPlayerPosLook(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY() - 255, sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), sPacketPlayerPosLook.getFlags(), sPacketPlayerPosLook.getTeleportId()));
			}
		}
		if (cancelAllCustomPayloads.isChecked()) {
			if (event.getPacket() instanceof SPacketCustomPayload) {
				event.setCanceled(true);
			}
		}
	}
}