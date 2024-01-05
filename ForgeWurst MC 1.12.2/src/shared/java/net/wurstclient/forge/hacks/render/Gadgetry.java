/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.SystemUtils;

import java.awt.*;

public final class Gadgetry extends Hack {

	private final CheckboxSetting debug =
			new CheckboxSetting("Debug", "Notifies you what the module is doing.",
					true);

	private final SliderSetting tick = new SliderSetting("Tick",
			"When a desired tick is reached, calculations and sets will be engaged.",
			500, 200, 5000, 100, SliderSetting.ValueDisplay.DECIMAL);

	private double renderSetting = 0;
	private double fpsLimit = 0;
	private boolean extremityMode = false;
	private double ramUsage = 0;
	private double renderDistance;
	private boolean boolSetting = false;

	public Gadgetry() {
		super("Gadgetry", "Enhances gameplay by improving FPS and performance via renders. \n" +
				"And some other methods.");
		setCategory(Category.RENDER);
		addSetting(debug);
		addSetting(tick);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		if (debug.isChecked()) {
			ChatUtils.message("AdvancedApparatus will manage game settings for you, ensuring maximum optimization. This module will determine the best settings based off system RAM usage. This module is targeted towards low-end computers and if your pc is over-kill, then this module will make little to no difference.");
			ChatUtils.warning("Debug mode might increase resource usage as it will introduce more operations");
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.ticksExisted % tick.getValue() == 0) {
			updateRAMUsage();
			updateRenderSetting();
			updateFPSLimitValue();
			updateExtremityModeStatus();
			updateRenderDistanceValue();
			updateCalcBoolVar();
			updateGameSettings();
		}
	}

	private void updateGameSettings() {
		if (mc.gameSettings.renderDistanceChunks != renderDistance) {
			mc.gameSettings.renderDistanceChunks = (int) renderDistance;

			if (debug.isChecked()) {
				ChatUtils.message("NEW RENDER DISTANCE VALUE: " + renderDistance);
			}
		}
		if (mc.gameSettings.clouds != renderSetting) {
			mc.gameSettings.clouds = (int) renderSetting;

			if (debug.isChecked()) {
				ChatUtils.message("NEW CLOUD SETTING VALUE: " + renderSetting);
			}
		}
		if (mc.gameSettings.ambientOcclusion != renderSetting) {
			mc.gameSettings.ambientOcclusion = (int) renderSetting;

			if (debug.isChecked()) {
				ChatUtils.message("NEW AO SETTING VALUE: " + renderSetting);
			}
		}
		if (mc.gameSettings.particleSetting != renderSetting) {
			mc.gameSettings.particleSetting = (int) renderSetting;

			if (debug.isChecked()) {
				ChatUtils.message("NEW PARTICLE SETTING VALUE: " + renderSetting);
			}
		}
		if (mc.gameSettings.limitFramerate != fpsLimit) {
			mc.gameSettings.limitFramerate = (int) fpsLimit;

			if (debug.isChecked()) {
				ChatUtils.message("NEW LF SETTING VALUE: " + fpsLimit);
			}
		}
		if (extremityMode && mc.gameSettings.fboEnable) {

			if (debug.isChecked()) {
				ChatUtils.message("Due to unusually high RAM usage, we have disabled FBO.");
			}

			mc.gameSettings.fboEnable = false;
		} else if (!extremityMode && !mc.gameSettings.fboEnable) {

			if (debug.isChecked()) {
				ChatUtils.message("RAM usage is now stable, re-initiating FBO.");
			}

			mc.gameSettings.fboEnable = true;
		}

		if (boolSetting) {
			mc.gameSettings.fancyGraphics = true;
			mc.gameSettings.entityShadows = true;
		} else {
			mc.gameSettings.fancyGraphics = false;
			mc.gameSettings.entityShadows = false;
		}

		if (debug.isChecked()) {
			ChatUtils.message("RAM-Usage: " + Math.round(ramUsage));
		}
	}

	private void updateRAMUsage() {
		ramUsage = SystemUtils.getUsedMemoryPercentage();
	}

	private void updateRenderDistanceValue() {
		renderDistance = calculateRenderDistance();
	}

	private int calculateRenderDistance() {
		int maxRenderDistance = 16;
		int minRenderDistance = 8;

		int calculatedRenderDistance = (int) (minRenderDistance + (maxRenderDistance - minRenderDistance) * (1 - ramUsage));

		return Math.min(maxRenderDistance, Math.max(minRenderDistance, calculatedRenderDistance));
	}


	private void updateRenderSetting() {
		renderSetting = calculateRenderSetting();
	}

	private int calculateRenderSetting() {
		double highUsageThreshold = 0.8;
		double lowUsageThreshold = 0.4;
		if (ramUsage >= highUsageThreshold) {
			return 2;
		} else if (ramUsage >= lowUsageThreshold) {
			return 1;
		} else {
			return 0;
		}
	}

	private void updateFPSLimitValue() {
		fpsLimit = calculateFPSLimit();
	}

	private int calculateFPSLimit() {
		double highUsageThreshold = 0.8;
		double moderateUsageThreshold = 0.4;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		DisplayMode dm = gd.getDisplayMode();
		int refreshRate = dm.getRefreshRate();

		if (ramUsage >= highUsageThreshold) {
			return (int) Math.ceil(refreshRate / 2.0); // Set to 50% of refresh rate
		} else if (ramUsage >= moderateUsageThreshold) {
			return (int) Math.ceil(refreshRate / 1.5); // Set to 75% of refresh rate
		} else {
			return refreshRate; // Set to the monitor refresh rate
		}
	}

	private void updateExtremityModeStatus() {
		extremityMode = shouldExtremityMode();
	}

	private boolean shouldExtremityMode() {
		return ramUsage >= 0.9;
	}

	private void updateCalcBoolVar() {
		boolSetting = calcBools();
	}

	private boolean calcBools() {
		double thres = 0.75;

		return ramUsage > thres;
	}
}
