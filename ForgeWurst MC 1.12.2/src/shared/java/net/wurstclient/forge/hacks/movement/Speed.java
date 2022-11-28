/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

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

	private final SliderSetting speed =
			new SliderSetting("Speed", "", 3, 0.1, 5, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting nbntstability =
			new CheckboxSetting("9b9t-Hop-Stability", "Makes 9b9t Hop slower but eaier to manage",
					false);

	private enum Mode {
		NCP("NCP", true, false, false, false, false, false, false),
		AAC("AAC", false, true, false, false, false, false, false),
		MINEPLEX("Mineplex", false, false, false, true, false, false, false),
		BASIC("Basic", false, false, true, false, false, false, false),
		STRAFEBYPASS("StrafeBypass", false, false, false, false, true, false, false),
		NBNT("9B9T-Hop", false, false, false, false, false, true, false),
		NBNTGROUND("9B9T-Ground", false, false, false, false, false, false, true);

		private final String name;
		private final boolean ncp;
		private final boolean aac;
		private final boolean basic;
		private final boolean mineplex;
		private final boolean strafebypass;
		private final boolean nbnt;
		private final boolean nbntground;

		private Mode(String name, boolean ncp, boolean aac, boolean basic, boolean mineplex, boolean strafebypass, boolean nbnt, boolean nbntground) {
			this.name = name;
			this.ncp = ncp;
			this.aac = aac;
			this.basic = basic;
			this.mineplex = mineplex;
			this.strafebypass = strafebypass;
			this.nbnt = nbnt;
			this.nbntground = nbntground;
		}

		public String toString() {
			return name;
		}
	}

	public Speed() {
		super("Speed", "I Show Speed");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(speed);
		addSetting(nbntstability);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
			if (mode.getSelected().basic) {
				MathUtils.speed(speed.getValueF());
			}
			if (mode.getSelected().ncp) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
				if (mc.player.onGround) {
					mc.player.jump();
					setTickLength(50 / 1.2f);
				} else {
					double[] dir = MathUtils.directionSpeed(0.19);
					mc.player.setSprinting(true);
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
					setTickLength(50);
				}
			}
			if (mode.getSelected().mineplex) {

			}
			if (mode.getSelected().nbnt) {
				if (!nbntstability.isChecked()) {
					if (mc.player.onGround) {
						setTickLength(50 / 10f);
						mc.player.jump();
					} else {
						setTickLength(50 / 5f);
						double[] dir = MathUtils.directionSpeed(0.19);
						mc.player.motionX = dir[0];
						mc.player.motionZ = dir[1];
					}
				} else {
					if (mc.player.onGround) {
						mc.player.jump();
						setTickLength(50 / 5f);
					} else {
						setTickLength(50 / 2f);
						double[] dir = MathUtils.directionSpeed(0.19);
						mc.player.motionX = dir[0];
						mc.player.motionZ = dir[1];
					}
				}
			}
			if (mode.getSelected().nbntground) {
				if (mc.player.onGround) {
					if (mc.player.ticksExisted % 2 == 0) {
						setTickLength(50 / 10f);
					} else {
						setTickLength(50 / 5f);
					}
				} else {
					setTickLength(50);
				}
			}
			if (mode.getSelected().strafebypass) {
				mc.player.setSprinting(true);
				if (mc.player.onGround) {
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
					mc.player.setVelocity(0, mc.player.motionY, 0);
					mc.player.jump();
				} else {
					double[] dir = MathUtils.directionSpeed(0.19);
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
				}
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
