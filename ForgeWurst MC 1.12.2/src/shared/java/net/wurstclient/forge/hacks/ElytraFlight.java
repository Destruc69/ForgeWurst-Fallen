/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;

import java.lang.reflect.Field;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", "Modes for ElytraFlight", Mode.values(), Mode.BOOST);

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", "Speed for going Up", 0.4, 0, 2, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("BaseSpeed", "Speed for going forwards, left, right and back", 0.4, 0, 2, 0.0005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", "Speed for going down", 0.4, 0, 2, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting fallSpeed =
			new SliderSetting("FallSpeed", "Fall speed", 0.04, 0, 0.02, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting vel =
			new CheckboxSetting("Velocity", "When jump and sneak are idle we keep you still in the air",
					false);

	private final SliderSetting pitch =
			new SliderSetting("Pitch", "Always maintain the same pitch, It i will be in packets", -90, -90, 90, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting takeoff =
			new SliderSetting("TakeOffTimerSpeed", 0.9, 0.1, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting whenfly =
			new SliderSetting("FlyingTimerSpeed", 0.9, 0.1, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	public ElytraFlight() {
		super("ElytraFlight", "Fly with elytras.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(fallSpeed);
		addSetting(vel);
		addSetting(pitch);
		addSetting(takeoff);
		addSetting(whenfly);
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
		setTickLength(50);
	}

	@SubscribeEvent
	public void update(WUpdateEvent event) {
		if (mc.player.isElytraFlying()) {
			if (!mc.gameSettings.keyBindJump.isKeyDown()) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.motionY = -fallSpeed.getValueF();
				} else {
					mc.player.motionY = -fallSpeed.getValueF() - mc.player.motionY;
				}
			}

			if (mode.getSelected().boost) {
				boost();
			}

			if (mode.getSelected().control) {
				control();
			}

			if (vel.isChecked()) {
				velocity();
			}
		}

		if (mc.player.fallDistance <= 1) {
			setTickLength(50 / takeoff.getValueF());
		} else if (mc.player.fallDistance > 1) {
			setTickLength(50 / whenfly.getValueF());
		}
	}

	public void boost() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.05;
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 0.05;
		}
		if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
			Minecraft.getMinecraft().player.motionY += upSpeed.getValueF();
		if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
			Minecraft.getMinecraft().player.motionY -= downSpeed.getValueF();
	}

	public void control() {
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY += upSpeed.getValueF();
		}

		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY -= downSpeed.getValueF();
		}

		if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0 || mc.player.moveVertical != 0) {
			double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
			mc.player.motionX = dir[0];
			mc.player.motionZ = dir[1];
		}
	}

	public void velocity() {
		if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = 0;
		}
	}

	@SubscribeEvent
	public void pitch(WPacketInputEvent event) {
		if (event.getPacket() instanceof CPacketPlayer.Rotation) {
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, pitch.getValueF(), mc.player.onGround));
		}
	}


	private enum Mode {
		BOOST("Boost", true, false),
		CONTROL("Control", false, true);

		private final String name;
		private final boolean boost;
		private final boolean control;

		private Mode(String name, boolean boost, boolean control) {
			this.name = name;
			this.boost = boost;
			this.control = control;
		}

		public String toString() {
			return name;
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
