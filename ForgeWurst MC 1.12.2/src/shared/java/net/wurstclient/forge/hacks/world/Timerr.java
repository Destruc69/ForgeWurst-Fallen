/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.Timer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.other.TPSTracker;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

public final class Timerr extends Hack {
	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed", 0.9, 0.1, 20, 0.1, ValueDisplay.DECIMAL);

	private final CheckboxSetting serverSync =
			new CheckboxSetting("ServerSync", "Syncs timer with the server TPS.",
					false);

	public Timerr() {
		super("Timer", "Changes the speed of almost everything.");
		setCategory(Category.WORLD);
		addSetting(timerSpeed);
		addSetting(serverSync);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50.0F);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (!serverSync.isChecked()) {
			setTickLength(50.0F / timerSpeed.getValueF());
		} else {
			float tps = (float) MathHelper.clamp(TPSTracker.getCurrentTPS(), 0.1, 20.0);
			setTickLength(1000.0F / tps);
		}
	}

	private void setTickLength(float tickLength)
	{
		try
		{
			Field fTimer = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);

			if(WMinecraft.VERSION.equals("1.10.2"))
			{
				Field fTimerSpeed = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);

			}else
			{
				Field fTickLength = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(mc), tickLength);
			}

		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
}