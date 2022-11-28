/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;

import java.lang.reflect.Field;

public final class Anchor extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.MOTION);

	public Anchor() {
		super("Anchor", "Fall over a hole faster and instantly.");
		setCategory(Category.COMBAT);
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
		if (mode.getSelected().motion) {
			if (mc.player.fallDistance > 1 || mc.player.fallDistance == 1) {
				mc.player.motionY -= 2;
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
			}
		}

		if (mode.getSelected().timer) {
			if (mc.player.fallDistance > 1 || mc.player.fallDistance == 1) {
				setTickLength(2);
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
			}
		}
	}

	private enum Mode {
		TIMER("Timer", true, false),
		MOTION("Motion", false, true);

		private final String name;
		private final boolean timer;
		private final boolean motion;

		private Mode(String name, boolean timer, boolean motion) {
			this.name = name;
			this.motion = motion;
			this.timer = timer;
		}

		public String toString() {
			return name;
		}
	}

	private void setTickLength(float tickLength) {
		try {
			Field fTimer = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);

			if (WMinecraft.VERSION.equals("1.10.2")) {
				Field fTimerSpeed = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);

			} else {
				Field fTickLength = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(mc), tickLength);
			}

		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}