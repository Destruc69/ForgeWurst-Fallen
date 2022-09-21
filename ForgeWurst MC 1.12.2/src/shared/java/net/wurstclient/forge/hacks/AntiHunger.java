/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class AntiHunger extends Hack {
	public AntiHunger() {
		super("AntiHunger", "Walk, its easier.");
		setCategory(Category.MOVEMENT);
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