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
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class AntiLag extends Hack {
	public AntiLag() {
		super("AntiLag", "Optimizes for best performance.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		mc.gameSettings.chatOpacity = 0;
		mc.gameSettings.enableVsync = true;
		mc.gameSettings.chatColours = false;
		mc.gameSettings.limitFramerate = 60;
		mc.gameSettings.forceUnicodeFont = false;
		mc.gameSettings.clouds = 0;
		mc.gameSettings.entityShadows = false;
		mc.gameSettings.fancyGraphics = false;
		mc.gameSettings.particleSetting = 0;
		mc.gameSettings.viewBobbing = false;
		mc.player.setInvisible(true);
		mc.player.setGlowing(false);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
}