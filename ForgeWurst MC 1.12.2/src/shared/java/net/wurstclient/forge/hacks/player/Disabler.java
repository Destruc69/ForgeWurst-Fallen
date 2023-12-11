/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class Disabler extends Hack {

	private final CheckboxSetting cPacketKeepAlive =
			new CheckboxSetting("CPacketKeepAlive",
					true);

	private final CheckboxSetting cPacketConfirmTransaction =
			new CheckboxSetting("CPacketConfirmTransaction",
					true);

	private final CheckboxSetting antiFlag =
			new CheckboxSetting("AntiFlag",
					true);

	private final CheckboxSetting sPacketConfirmTransaction =
			new CheckboxSetting("SPacketConfirmTransaction",
					true);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(cPacketKeepAlive);
		addSetting(cPacketConfirmTransaction);
		addSetting(antiFlag);
		addSetting(sPacketConfirmTransaction);
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
		if (cPacketKeepAlive.isChecked()) {
			if (event.getPacket() instanceof CPacketKeepAlive) {
				mc.player.connection.sendPacket(new CPacketKeepAlive(Integer.MIN_VALUE + Math.round(Math.random() * 100)));
				event.setCanceled(true);
			}
		}
		if (cPacketConfirmTransaction.isChecked()) {
			if (event.getPacket() instanceof CPacketConfirmTransaction) {
				CPacketConfirmTransaction confirmTransaction = (CPacketConfirmTransaction) event.getPacket();
				mc.player.connection.sendPacket(new CPacketConfirmTransaction(Integer.MAX_VALUE, confirmTransaction.getUid(), false));
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (antiFlag.isChecked()) {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				event.setPacket(new SPacketPlayerPosLook(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY() - Integer.MAX_VALUE, sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), sPacketPlayerPosLook.getFlags(), sPacketPlayerPosLook.getTeleportId()));
			}
		}
		if (sPacketConfirmTransaction.isChecked()) {
			if (event.getPacket() instanceof SPacketConfirmTransaction) {
				SPacketConfirmTransaction sPacketConfirmTransaction = (SPacketConfirmTransaction) event.getPacket();
				if (sPacketConfirmTransaction.getActionNumber() < 0) {
					event.setCanceled(true);
				}
			}
		}
	}
}