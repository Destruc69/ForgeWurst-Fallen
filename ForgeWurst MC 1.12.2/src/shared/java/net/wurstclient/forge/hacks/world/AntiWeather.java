/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.EntityFakePlayer;

public final class AntiWeather extends Hack {

	private final CheckboxSetting opposite =
			new CheckboxSetting("Opposite", "Pro-Weather.",
					false);

	public AntiWeather() {
		super("AntiWeather", "Removes rain, ect.");
		setCategory(Category.WORLD);
		addSetting(opposite);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		if (opposite.isChecked()) {
			mc.world.rainingStrength = 0f;
			mc.world.prevRainingStrength = 0f;
			mc.world.thunderingStrength = 0f;
			mc.world.prevThunderingStrength = 0f;
		}
	}

	@SubscribeEvent
	public void update(WUpdateEvent event) {
		if (!opposite.isChecked()) {
			mc.world.rainingStrength = 0f;
			mc.world.prevRainingStrength = 0f;
			mc.world.thunderingStrength = 0f;
			mc.world.prevThunderingStrength = 0f;
		} else {
			mc.world.rainingStrength = 2f;
			mc.world.prevRainingStrength = 2f;
			mc.world.thunderingStrength = 2f;
			mc.world.prevThunderingStrength = 2f;
		}
	}
}