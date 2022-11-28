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
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;

public final class Disabler extends Hack {
	private final CheckboxSetting ping =
			new CheckboxSetting("CPacketPing cancel", "Cancels this packet, the server doesnt know the client time",
					false);

	private final CheckboxSetting confirmTP =
			new CheckboxSetting("ConfirmTeleport", "Sends a packet confirm pos",
					false);

	private final CheckboxSetting stat =
			new CheckboxSetting("CPacketClientStatus/CPacketClientSettings cancel", "Cancels this packet, the server knows less about clients status",
					false);

	private final CheckboxSetting ghostly =
			new CheckboxSetting("Ghostly", "Dont send some packets so the AntiCheat has less info on you",
					false);

	private final CheckboxSetting pingSpoof =
			new CheckboxSetting("PingSpoof", "Higher ping means the anti cheat will be less harsh on you",
					false);

	private final SliderSetting pingDelay =
			new SliderSetting("PingSpoofDelay [MS]", "Every time we reach this time we send the packet to ping spoof", 40, 0, 1000, 5, SliderSetting.ValueDisplay.DECIMAL);


	ArrayList<Packet> packets = new ArrayList<>();

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(ping);
		addSetting(confirmTP);
		addSetting(stat);
		addSetting(ghostly);
		addSetting(pingSpoof);
		addSetting(pingDelay);
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

	}
	@SubscribeEvent
	public void pingSpoof(WPacketInputEvent event) {
		try {
			if (!pingSpoof.isChecked())
				return;

			if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation) {
				packets.add(event.getPacket());
				event.setCanceled(true);
			}

			if (mc.player.ticksExisted % pingDelay.getValueF() != 0) {
				for (Packet packet : packets) {
					assert packet != null;
					mc.player.connection.sendPacket(packet);
					packets.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (confirmTP.isChecked()) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
					mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		try {
			if (confirmTP.isChecked()) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
					mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
				}
			}
			if (stat.isChecked()) {
				if (event.getPacket() instanceof CPacketClientSettings || event.getPacket() instanceof CPacketClientStatus) {
					event.setCanceled(true);
				}
			}
			if (ghostly.isChecked()) {
				if (event.getPacket() instanceof CPacketConfirmTransaction || event.getPacket() instanceof CPacketEntityAction || event.getPacket() instanceof CPacketCustomPayload) {
					event.setCanceled(true);
				}
				if (!pingSpoof.isChecked()) {
					if (event.getPacket() instanceof CPacketKeepAlive) {
						event.setCanceled(true);
					}
				}
			}
			if (ping.isChecked()) {
				if (event.getPacket() instanceof CPacketPing) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}