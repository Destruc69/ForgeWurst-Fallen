/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class Disabler extends Hack {

	private final CheckboxSetting plusMode =
			new CheckboxSetting("Plus", "Cancels more packets, these packets deppending on the server if canceled \n" +
					"may kick you.",
					false);

	private final CheckboxSetting cancelKeepAlive =
			new CheckboxSetting("CancelKeepAlive", "Cancels the keep alive packets, risky and servers might kick you",
					false);

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(plusMode);
		addSetting(cancelKeepAlive);
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
		try {
			if (!plusMode.isChecked()) {
				if (event.getPacket() instanceof CPacketConfirmTransaction || event.getPacket() instanceof CPacketCustomPayload) {
					event.setCanceled(true);
				}
			} else {
				if (event.getPacket() instanceof CPacketConfirmTransaction || event.getPacket() instanceof CPacketCustomPayload ||
						event.getPacket() instanceof CPacketEntityAction) {
					event.setCanceled(true);
				}
			}
			if (cancelKeepAlive.isChecked()) {
				if (event.getPacket() instanceof CPacketKeepAlive) {
					event.setCanceled(true);
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
			}
		} catch (Exception ignored) {
		}
	}
}