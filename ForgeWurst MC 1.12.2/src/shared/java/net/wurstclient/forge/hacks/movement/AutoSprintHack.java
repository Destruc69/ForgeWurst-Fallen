/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class AutoSprintHack extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.RAGE);

	public AutoSprintHack() {
		super("AutoSprint", "Makes you sprint automatically.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + mode.getSelected().name() + "]";
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
		if (mode.getSelected().rage) {
			mc.player.setSprinting(mc.player.moveForward != 0 || mc.player.moveStrafing != 0);
		} else {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, true);
		}
	}

	private enum Mode {
		NORMAL("Normal", true, false),
		RAGE("Rage", false, true);

		private final String name;
		private final boolean normal;
		private final boolean rage;

		private Mode(String name, boolean normal, boolean rage) {
			this.name = name;
			this.normal = normal;
			this.rage = rage;
		}

		public String toString() {
			return name;
		}
	}
}