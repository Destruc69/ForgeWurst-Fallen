/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;

import java.lang.reflect.Field;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public final class Speed extends Hack {

	public final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NCP);

	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed [NCP-FAST]", "How fast is the timer for ncp fast", 3, 1.1, 5, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private enum Mode {
		NCP("NCP", true, false),
		AAC("AAC", false, true);

		private final String name;
		private final boolean ncp;
		private final boolean aac;

		private Mode(String name, boolean ncp, boolean aac) {
			this.name = name;
			this.ncp = ncp;
			this.aac = aac;
		}

		public String toString() {
			return name;
		}
	}

	public Speed() {
		super("Speed", "I Show Speed");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(timerSpeed);
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
		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
			if (mode.getSelected().ncp) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
				if (mc.player.onGround) {
					mc.player.jump();
				}
				double[] dir = MathUtils.directionSpeed(0.19);
				mc.player.setSprinting(true);
				mc.player.motionX = dir[0];
				mc.player.motionZ = dir[1];
			}
			if (mode.getSelected().aac) {
				if (mc.player.onGround) {
					mc.player.jump();
					mc.player.motionY = 0.405;
					mc.player.motionX *= 1.004;
					mc.player.motionZ *= 1.004;
					return;
				}
				mc.player.setSprinting(true);
				double yaw = mc.player.rotationYaw;
				mc.player.motionX = -sin(yaw) * 0.19;
				mc.player.motionZ = cos(yaw) * 0.19;
			}

		}
	}
}