/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.TimerUtils;

public final class AntiAFK extends Hack {

	public AntiAFK() {
		super("AntiAFK", "Prevents getting kicked for idling");
		setCategory(Category.MOVEMENT);
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
		if (mc.player.ticksExisted % 3 == 0) {
			mc.player.rotationYaw = mc.player.rotationYaw + 5;
			mc.player.rotationPitch = mc.player.rotationPitch + 1;
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		} else {
			mc.player.rotationPitch = mc.player.rotationPitch - 1;
			mc.player.rotationYaw = mc.player.rotationYaw - 2;
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
		}
	}
}