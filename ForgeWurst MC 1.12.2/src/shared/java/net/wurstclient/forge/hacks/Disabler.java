/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

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
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.TimerUtils;

import java.util.ArrayList;

public final class Disabler extends Hack {

	private final CheckboxSetting ability =
			new CheckboxSetting("AbilitySpoof", "Sends a packet that flying is allowed",
					false);
	
	private final CheckboxSetting ping =
			new CheckboxSetting("CPacketPing cancel", "Cancels this packet, the server doesnt know the client time",
					false);

	private final CheckboxSetting confirmTP =
			new CheckboxSetting("ConfirmTeleport", "Sends a packet confirm pos",
					false);

	private final CheckboxSetting stat =
			new CheckboxSetting("CPacketClientStatus/CPacketClientSettings cancel", "Cancels this packet, the server knows less about clients status",
					false);

	private final CheckboxSetting spectator =
			new CheckboxSetting("SpectatorSpoof", "Sends a packet that your a spectator",
					false);

	private final CheckboxSetting inputSpoof =
			new CheckboxSetting("InputSpoof", "Sends a packet that makes your... its hard to explain. It spoofs your movement to its max",
					false);

	private final CheckboxSetting groundSpoof =
			new CheckboxSetting("GroundSpoof", "Every oncoming packet is set to onground",
					false);

	private final CheckboxSetting ghostly =
			new CheckboxSetting("Ghostly", "Dont send some packets so the AntiCheat has less info on you",
					false);

	private final CheckboxSetting pingSpoof =
			new CheckboxSetting("PingSpoof", "Higher ping means the anti cheat will be less harsh on you",
					false);

	private final SliderSetting pingDelay =
			new SliderSetting("PingSpoofDelay [MS]", "Every time we reach this time we send the packet to ping spoof", 40, 0, 5000, 5, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting fuck =
			new CheckboxSetting("FuckTheServer", "Who cares where the server thinks we should be, just gonna ignore it (might bypass shitty ac)",
					false);

	private final CheckboxSetting fuck2 =
			new CheckboxSetting("FuckTheServer 2.0", "WHO THE FUCK CARES ABOUT ANYTHING THE SERVER SAYS (cancels all spackets)",
					false);

	private final CheckboxSetting paulssettings =
			new CheckboxSetting("PaulsSettings", "my (who is writing this) recommended settings\n" +
					"Its a LessFlag setting, Meaning a Lite Disabler",
					false);

	ArrayList<Packet> packets = new ArrayList<>();

	public Disabler() {
		super("Disabler", "Bypass anti cheats.");
		setCategory(Category.PLAYER);
		addSetting(ability);
		addSetting(ping);
		addSetting(confirmTP);
		addSetting(stat);
		addSetting(spectator);
		addSetting(inputSpoof);
		addSetting(groundSpoof);
		addSetting(ghostly);
		addSetting(pingSpoof);
		addSetting(pingDelay);
		addSetting(fuck);
		addSetting(fuck2);
		addSetting(paulssettings);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (!ForgeWurst.getForgeWurst().getHax().killaura.isEnabled() && !ForgeWurst.getForgeWurst().getHax().cheststealer.isEnabled()) {
			TimerUtils.reset();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		abilitySpoof();
		spectatorSpoof();
		inputSpoof();

		if (paulssettings.isChecked()) {
			ability.setChecked(false);
			ping.setChecked(true);
			spectator.setChecked(false);
			inputSpoof.setChecked(false);
			groundSpoof.setChecked(true);
			ghostly.setChecked(true);
			pingSpoof.setChecked(true);
			pingDelay.setValue(25);
			fuck.setChecked(false);
			fuck2.setChecked(false);
			stat.setChecked(true);
			paulssettings.setChecked(false);
		}
	}
	@SubscribeEvent
	public void pingSpoof(WPacketInputEvent event) {
		if (!pingSpoof.isChecked())
			return;

		if (!ForgeWurst.getForgeWurst().getHax().killaura.isEnabled() && !ForgeWurst.getForgeWurst().getHax().cheststealer.isEnabled()) {
			if (TimerUtils.getTimePassed() < pingDelay.getValueF()) {
				if (event.getPacket() instanceof CPacketPlayer) {
					packets.add(event.getPacket());
					event.setCanceled(true);
				}
			} else if (TimerUtils.getTimePassed() > pingDelay.getValueF()) {
				TimerUtils.reset();
				for (Packet thePacket : packets) {
					mc.player.connection.sendPacket(thePacket);
					packets.clear();
				}
			}
		}
	}

	public void abilitySpoof() {
		if (!ability.isChecked())
			return;
		mc.player.capabilities.allowFlying = true;

		mc.player.connection.sendPacket(new CPacketPlayerAbilities(mc.player.capabilities));
	}

	public void spectatorSpoof() {
		if (!spectator.isChecked())
			return;
		mc.player.connection.sendPacket(new CPacketSpectate(mc.player.getUniqueID()));
	}

	public void inputSpoof() {
		if (!inputSpoof.isChecked())
			return;
		mc.player.connection.sendPacket(new CPacketInput(Float.MAX_VALUE, Float.MAX_VALUE, mc.gameSettings.keyBindJump.isKeyDown(), mc.gameSettings.keyBindSneak.isKeyDown()));
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
			if (fuck.isChecked()) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
				}
			}
			if (fuck2.isChecked()) {
				event.setCanceled(true);
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
			if (groundSpoof.isChecked()) {
				if (event.getPacket() instanceof CPacketPlayer.Position) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
				}
				if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
				}
				if (event.getPacket() instanceof CPacketPlayer.Rotation) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
				}
			}
			if (stat.isChecked()) {
				if (event.getPacket() instanceof CPacketClientSettings || event.getPacket() instanceof CPacketClientStatus) {
					event.setCanceled(true);
				}
			}
			if (ghostly.isChecked()) {
				if (event.getPacket() instanceof CPacketKeepAlive || event.getPacket() instanceof CPacketConfirmTransaction || event.getPacket() instanceof CPacketEntityAction || event.getPacket() instanceof CPacketCustomPayload) {
					event.setCanceled(true);
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