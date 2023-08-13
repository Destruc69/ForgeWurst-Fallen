/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.hud;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class CoordinatesModule extends Hack {

	private final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.DECIMAL);


	public CoordinatesModule() {
		super("Coordinates", "Shows coordinate values.");
		setCategory(Category.HUD);
		addSetting(x);
		addSetting(y);
		addSetting(z);
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
		x.setValue(Math.round(mc.player.lastTickPosX));
		y.setValue(Math.round(mc.player.lastTickPosY));
		z.setValue(Math.round(mc.player.lastTickPosZ));
	}
}