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
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class Giant extends Hack {
	private final SliderSetting height =
			new SliderSetting("Height", 2, 1, 30, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting width =
			new SliderSetting("Width", 2, 1, 30, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Giant() {
		super("Giant", "Makes you really big.");
		setCategory(Category.PLAYER);
		addSetting(height);
		addSetting(width);
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
		event.getPlayer().height = -height.getValueF();
		event.getPlayer().width = width.getValueF();
	}
}