/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

import java.util.Objects;

public final class DataFuelModule extends Hack {

	private final long prevTime;
	private final float[] ticks = new float[20];
	private int currentTick;

	private final SliderSetting tps =
			new SliderSetting("TPS", 0, 0, 20, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting ping =
			new SliderSetting("Ping", 0, 0, 2500, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting fps =
			new SliderSetting("FPS", 0, 0, 2500, 1, SliderSetting.ValueDisplay.DECIMAL);

	public DataFuelModule() {
		super("DataFuel", "Shows the TPS of the server\n" +
				", your ping and your FPS");

		this.prevTime = -1;

		for (int i = 0, len = this.ticks.length; i < len; i++)
		{
			this.ticks[i] = 0.0f;
		}

		setCategory(Category.HUD);
		addSetting(tps);
		addSetting(ping);
		addSetting(fps);
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
		try {
			tps.setValue(getTickRate());
			ping.setValue(Objects.requireNonNull(mc.getCurrentServerData()).pingToServer);
			fps.setValue(Minecraft.getDebugFPS());
		} catch (Exception ignored) {
			tps.setValue(0);
			ping.setValue(0);
			fps.setValue(Minecraft.getDebugFPS());
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		if (event.getPacket() instanceof SPacketTimeUpdate) {
			if (prevTime != -1) {
				this.ticks[this.currentTick % this.ticks.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - this.prevTime) / 1000.0f)), 0.0f, 20.0f);
				this.currentTick++;
			}
		}
	}

	public float getTickRate()
	{
		int tickCount = 0;
		float tickRate = 0.0f;

		for (final float tick : this.ticks) {
			if (tick > 0.0f) {
				tickRate += tick;
				tickCount++;
			}
		}

		return MathHelper.clamp((tickRate / tickCount), 0.0f, 20.0f);
	}
}