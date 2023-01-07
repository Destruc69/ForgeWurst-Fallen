/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;

public final class AntiHunger extends Hack {
	private final CheckboxSetting force =
			new CheckboxSetting("Force", "",
					false);

	private final CheckboxSetting spoof =
			new CheckboxSetting("Spoof", "Spoofs games settings, ect. So the server thinks your standing still thus \n" +
					"wont give you hunger packets, Might now work on all anti-cheats.",
					false);

	public AntiHunger() {
		super("AntiHunger", "Walk, its easier.");
		setCategory(Category.MOVEMENT);
		addSetting(force);
		addSetting(spoof);
	}

	@Override
	protected void onEnable() {
		try {
			MinecraftForge.EVENT_BUS.register(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (force.isChecked()) {
			if (mc.player.isSprinting()) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
			}
		}
		if (spoof.isChecked()) {
			mc.player.connection.sendPacket(new CPacketInput(0, 0, false, false));
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof CPacketEntityAction) {
				CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
				if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING)) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (event.getPacket() instanceof CPacketEntityAction) {
				CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
				if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING)) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}