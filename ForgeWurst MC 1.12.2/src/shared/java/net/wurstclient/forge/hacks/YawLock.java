/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class YawLock extends Hack {

	private final SliderSetting yaw =
			new SliderSetting("Yaw", 1, 1.0, 360.0, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting pitch =
			new SliderSetting("Pitch", 1, -90, 90, 1.0, SliderSetting.ValueDisplay.DECIMAL);

	public YawLock() {
		super("YawLock", "Choose the rotation in settings.");
		setCategory(Category.MOVEMENT);
		addSetting(yaw);
		addSetting(pitch);
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
		mc.player.rotationYaw = yaw.getValueF();
		mc.player.rotationPitch = pitch.getValueF();
	}
}
