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
import net.wurstclient.forge.utils.MathUtils;

public final class Phase extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.RAGE);

	public Phase() {
		super("Phase", "Allows you too go through blocks.");
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
		if (mode.getSelected().noclip) {
			noClip();
		}
		if (mode.getSelected().teleport) {
			teleport();
		}
	}

	public void noClip() {
		mc.player.motionY = 0;
		mc.player.noClip = true;
	}

	public void teleport() {
		double[] dir = MathUtils.directionSpeed(1);
		double x = mc.player.posX + dir[0];
		double z = mc.player.posZ + dir[1];
		mc.player.motionY = 0;
		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
			mc.player.setPosition(x, mc.player.posY, z);
		}
	}

	private enum Mode {
		NORMAL("NoClip", true, false),
		RAGE("Teleport", false, true);

		private final String name;
		private final boolean noclip;
		private final boolean teleport;

		private Mode(String name, boolean noclip, boolean teleport) {
			this.name = name;
			this.noclip = noclip;
			this.teleport = teleport;
		}

		public String toString() {
			return name;
		}
	}
}