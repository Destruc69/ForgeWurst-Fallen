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
import net.wurstclient.forge.settings.SliderSetting;

public final class HighJump extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", "Greater the speed means higher the jump", 0.2, 0.1, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	public HighJump() {
		super("HighJump", "Jump higher.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private boolean a;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.motionY > 0) {
			if (!a) {
				mc.player.motionY = speed.getValue();
				a = true;
			}
		} else {
			a = false;
		}
	}
}