/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class Animations extends Hack {

	private final SliderSetting xv =
			new SliderSetting("X", 40, -360 - 360, 360 * 2, 5, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting yv =
			new SliderSetting("Y", 40, -360 - 360, 360 * 2, 5, SliderSetting.ValueDisplay.DECIMAL);


	private final CheckboxSetting swing =
			new CheckboxSetting("OnlyOnSwing", "Dont send some packets so the AntiCheat has less info on you",
					false);

	public Animations() {
		super("ArmModel", "Change arm model pos");
		setCategory(Category.PLAYER);
		addSetting(xv);
		addSetting(yv);
		addSetting(swing);
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
		if (!swing.isChecked()) {
			mc.player.renderArmYaw = xv.getValueF();
			mc.player.renderArmPitch = yv.getValueF();
		} else {
			if (mc.player.isSwingInProgress) {
				mc.player.renderArmYaw = xv.getValueF();
				mc.player.renderArmPitch = yv.getValueF();
			}
		}
	}
}