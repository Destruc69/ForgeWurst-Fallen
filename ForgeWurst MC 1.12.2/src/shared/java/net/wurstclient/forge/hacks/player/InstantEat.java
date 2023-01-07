/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.server.management.DemoPlayerInteractionManager;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import stevebot.player.PlayerUtils;

import java.lang.reflect.Field;

public final class InstantEat extends Hack {

	public InstantEat() {
		super("InstantEat", "Eat food instantly.");
		setCategory(Category.PLAYER);
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
		try
		{
			Field foodSpeed = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_75123_d" : "foodTimer");
			foodSpeed.setAccessible(true);
			foodSpeed.setDouble(0, 0);
			foodSpeed.setInt(0, 0);
		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
}