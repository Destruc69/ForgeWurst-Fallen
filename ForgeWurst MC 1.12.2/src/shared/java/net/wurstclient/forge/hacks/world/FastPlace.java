/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import java.lang.reflect.Field;

import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class FastPlace extends Hack
{
	public FastPlace()
	{
		super("FastPlace", "Allows you to place blocks 5 times faster.");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event)
	{
		try
		{
			Field rightClickDelayTimer =
					mc.getClass().getDeclaredField(wurst.isObfuscated()
							? "field_71467_ac" : "rightClickDelayTimer");
			rightClickDelayTimer.setAccessible(true);
			rightClickDelayTimer.setInt(mc, 0);

		}catch(ReflectiveOperationException e)
		{
			setEnabled(false);
			throw new RuntimeException(e);
		}
	}
}