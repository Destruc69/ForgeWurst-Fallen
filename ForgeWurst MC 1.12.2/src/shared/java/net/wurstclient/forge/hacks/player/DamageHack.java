/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class DamageHack extends Hack {

	public DamageHack() {
		super("Damage", "Damages the player.");
		setCategory(Category.PLAYER);
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
		if (mc.player.ticksExisted % 2 == 0) {
			for (int a = 0; a < 250; a ++) {
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY - 0.0000000001, mc.player.lastTickPosZ);
			}
		} else {
			for (int a = 0; a < 250; a ++) {
				mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY + 0.0000000001, mc.player.lastTickPosZ);
			}
		}
	}
}