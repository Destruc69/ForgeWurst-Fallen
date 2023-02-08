/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

public final class AutoPilot extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private enum Mode {
		NORMAL("Basic", true, false),
		ADVANCED("Advanced", false, true);

		private final String name;
		private final boolean basic;
		private final boolean advanced;

		private Mode(String name, boolean basic, boolean advanced) {
			this.name = name;
			this.basic = basic;
			this.advanced = advanced;
		}

		public String toString() {
			return name;
		}
	}

	public AutoPilot() {
		super("AutoPilot", "Simple automation for navigation.");
		setCategory(Category.PATHING);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (mode.getSelected().advanced) {
			engageStartUpForAdvanced();
		} else if (mode.getSelected().basic) {
			engageStartUpForBasic();
		}
		setEnabled(false);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {

	}

	public static void engageStartUpForBasic() {
		double yaw = mc.player.rotationYaw;
		if (yaw >= 0 && yaw < 90) {
			mc.player.rotationYaw = 0;
		} else if (yaw >= 90 && yaw < 180) {
			mc.player.rotationYaw = 90;
		} else if (yaw >= 180 && yaw < 360) {
			mc.player.rotationYaw = 180;
		} else {
			mc.player.rotationYaw = 360;
		}
	}

	public static void engageStartUpForAdvanced() {

	}
}