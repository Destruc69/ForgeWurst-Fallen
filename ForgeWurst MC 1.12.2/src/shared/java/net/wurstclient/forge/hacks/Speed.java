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
import net.wurstclient.forge.utils.MathUtils;

import java.lang.reflect.Field;

public final class Speed extends Hack {

	public final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NCP);

	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed [NCP-FAST]", "How fast is the timer for ncp fast", 3, 1.1, 5, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private enum Mode {
		NCP("NCP", true, false, false, false, false),
		NCPFAST("NCP-Fast", false, true, false, false, false),
		NCPFAST2("NCP-LowHop", false, false, true, false, false),
		HYPIXEL("Better NCP (use this)", false, false, false, true, false),
		MINEPLEX("Minplex", false, false, false, false, true);

		private final String name;
		private final boolean ncp;
		private final boolean ncpfast;
		private final boolean ncpfast2;
		private final boolean hypixel;
		private final boolean mineplex;

		private Mode(String name, boolean ncp, boolean ncpfast, boolean ncpfast2, boolean hypixel, boolean mineplex) {
			this.name = name;
			this.ncp = ncp;
			this.ncpfast = ncpfast;
			this.ncpfast2 = ncpfast2;
			this.hypixel = hypixel;
			this.mineplex = mineplex;
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
		setTickLength(50);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
			if (mode.getSelected().ncp) {
				mc.player.setSprinting(true);
				if (mc.player.onGround) {
					mc.player.motionY = 0.405;
					double[] dir = MathUtils.directionSpeed(0.19);
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
				}
			}
			if (mode.getSelected().mineplex) {
				if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
					if (mc.player.onGround) {
						mc.player.jump();
						setTickLength(50);
					} else if (mc.player.isAirBorne) {
						setTickLength(50 / 2f);
						double[] dir = MathUtils.directionSpeed(0.1);
						mc.player.motionX = dir[0];
						mc.player.motionZ = dir[1];
						mc.player.setSprinting(true);
					}
				}
			}
			if (mode.getSelected().hypixel) {
				double[] dir = MathUtils.directionSpeed(0.19);
				mc.player.motionX = dir[0];
				mc.player.motionZ = dir[1];
				if (mc.player.onGround) {
					mc.player.jump();
				}
				mc.player.setSprinting(true);
			}
			if (mode.getSelected().ncpfast2) {
				mc.player.setSprinting(true);
				if (mc.player.onGround) {
					setTickLength(50 / 0.5f);
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
					mc.player.setPosition(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ);
					double[] dir = MathUtils.directionSpeed(0.19);
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
				} else {
					setTickLength(50);
				}
			}
			if (mode.getSelected().ncpfast) {
				mc.player.setSprinting(true);
				if (mc.player.onGround) {
					setTickLength(50 / timerSpeed.getValueF());
					mc.player.motionY = 0.405;
					double[] dir = MathUtils.directionSpeed(0.19);
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
					mc.player.jump();
				} else {
					setTickLength(50);
				}
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
