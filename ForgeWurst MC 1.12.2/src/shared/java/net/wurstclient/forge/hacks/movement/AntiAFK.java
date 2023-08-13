/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class AntiAFK extends Hack {

	private final CheckboxSetting crazy =
			new CheckboxSetting("CrazyMode", "Just a crazy version of the anti afk",
					false);

	public AntiAFK() {
		super("AntiAFK", "Prevents getting kicked for idling");
		setCategory(Category.MOVEMENT);
		addSetting(crazy);
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
		if (!crazy.isChecked()) {
			if (mc.player.ticksExisted % 5 == 0) {
				mc.player.rotationYaw++;
				if (mc.player.onGround) {
					mc.player.jump();
				}
			}
		} else {
			mc.player.rotationYaw = mc.player.rotationYaw + Math.round(Math.random() * 90);
			mc.player.rotationPitch = Math.round(Math.random() * 90);
		}
	}
}