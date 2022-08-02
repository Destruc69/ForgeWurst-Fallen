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
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.TimerUtils;

public final class AntiAFK extends Hack {

	private final SliderSetting delay =
			new SliderSetting("Delay MS", 1000, 300, 2500, 100, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting jump =
			new CheckboxSetting("Jump",
					false);

	private final CheckboxSetting yaw =
			new CheckboxSetting("Yaw",
					false);

	private final CheckboxSetting pitch =
			new CheckboxSetting("Pitch",
					false);

	private final CheckboxSetting sneak =
			new CheckboxSetting("Sneak",
					false);

	public AntiAFK() {
		super("AntiAFK", "Prevents getting kicked for idling");
		setCategory(Category.MOVEMENT);
		addSetting(delay);
		addSetting(jump);
		addSetting(yaw);
		addSetting(pitch);
		addSetting(sneak);
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
		if (TimerUtils.hasReached(delay.getValueI())) {
			if (yaw.isChecked()) {
				mc.player.rotationYaw = mc.player.rotationYaw + 5;
			}

			if (sneak.isChecked()) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
			}

			if (pitch.isChecked()) {
				mc.player.rotationPitch = 90;
			}

			if (jump.isChecked()) {
				if (mc.player.onGround) {
					mc.player.jump();
				}
			}

			TimerUtils.reset();
		} else {
			if (sneak.isChecked()) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
			}

			if (pitch.isChecked()) {
				mc.player.rotationPitch = -90;
			}
		}
	}
}