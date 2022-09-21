/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.MathUtils;

import javax.sound.sampled.Clip;
import java.lang.reflect.Field;

public final class ElytraFlight extends Hack {

	public static double startYyyyy;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", "Modes for ElytraFlight", Mode.values(), Mode.BOOST);

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", "Speed for going Up", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("BaseSpeed", "Speed for going forwards, left, right and back", 0.4, 0, 2, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", "Speed for going down", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final EnumSetting<TakeOffMode> takeoffmode =
			new EnumSetting<>("TakeOffMode", "Take off modes", TakeOffMode.values(), TakeOffMode.START);

	private final SliderSetting fallSpeed =
			new SliderSetting("FallSpeed", "How fast we move down when space/sneak is not pressed", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting ncpstrict =
			new CheckboxSetting("NCPStrict", "Bypass NCP",
					false);

	private final SliderSetting timerTakeOffSpeed =
			new SliderSetting("TimerTakeOffSpeed", "Timer for when taking off",0.9, 0.1, 2, 0.001, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting timerWhenFlying =
			new SliderSetting("TimerWhenFlyingSpeed", "Timer for when flying",0.9, 0.1, 2, 0.001, SliderSetting.ValueDisplay.DECIMAL);


	public ElytraFlight() {
		super("ElytraFlight", "Fly with elytras.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(takeoffmode);
		addSetting(fallSpeed);
		addSetting(ncpstrict);
		addSetting(timerTakeOffSpeed);
		addSetting(timerWhenFlying);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			startYyyyy = mc.player.posY;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50);
	}

	@SubscribeEvent
	public void update(WUpdateEvent event) {
		if (!mc.player.isElytraFlying()) {
			setTickLength(50 / timerTakeOffSpeed.getValueF());
		} else {
			setTickLength(50 / timerWhenFlying.getValueF());
		}
		if (takeoffmode.getSelected().start) {
			autoTakeOffSTART();
		}
		if (takeoffmode.getSelected().always) {
			autoTakeOffALWAYS();
		}
		if (!mc.player.isElytraFlying()) {
			startYyyyy = mc.player.posY;
		}
		if (!mc.player.isElytraFlying())
			return;
		if (ncpstrict.isChecked()) {
			mc.player.setPosition(mc.player.posX, startYyyyy, mc.player.posZ);
		}
		if (mode.getSelected().boost || mode.getSelected().boostnoy) {
			boostElytraFly();
		}
		if (mode.getSelected().control) {
			controlElytraFly();
		}
		if (!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY -= fallSpeed.getValueF();
		}
	}

	public void autoTakeOffSTART() {
		if (!mc.player.isElytraFlying()) {
			if (!isUnderAir())
				return;
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
		}
	}

	public void autoTakeOffALWAYS() {
		if (!mc.player.isElytraFlying()) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
		}
	}

	public boolean isUnderAir() {
		BlockPos under = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
		if (mc.world.getBlockState(under).getBlock().equals(Blocks.AIR)) {
			return true;
		} else {
			return false;
		}
	}

	public void boostElytraFly() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			if (!mode.getSelected().boostnoy) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * baseSpeed.getValueF();
			}
		}
		if (!mode.getSelected().boostnoy) {
			if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
				Minecraft.getMinecraft().player.motionY += upSpeed.getValueF();
			if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
				Minecraft.getMinecraft().player.motionY -= downSpeed.getValueF();
		}
	}

	public void controlElytraFly() {
		double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
		mc.player.motionX = dir[0];
		mc.player.motionZ = dir[1];
		if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0 && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = 0;
			mc.player.motionX = 0;
			 mc.player.motionZ = 0;
		} else {
			if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
				mc.player.motionX = dir[0];
				mc.player.motionZ = dir[1];
			}
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY += upSpeed.getValueF();
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.player.motionY -= downSpeed.getValueF();
			}
		}
		if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = 0;
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (!ncpstrict.isChecked())
			return;
		if (event.getPacket() instanceof CPacketPlayer.Rotation) {
			event.setCanceled(true);
		}
		if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
			event.setCanceled(true);
			CPacketPlayer.PositionRotation cPacketPlayer = (CPacketPlayer.PositionRotation) event.getPacket();
			mc.player.connection.sendPacket(new CPacketPlayer.Position(cPacketPlayer.getX(0), cPacketPlayer.getY(0), cPacketPlayer.getZ(0), mc.player.onGround));
		}
	}

	private enum Mode {
		BOOST("Boost", true, false, false),
		BOOSTNOY("Boost-NO-Y", false, false, true),
		CONTROL("Control", false, true, false);

		private final String name;
		private final boolean boost;
		private final boolean control;
		private final boolean boostnoy;

		private Mode(String name, boolean boost, boolean control, boolean boostnoy) {
			this.name = name;
			this.boost = boost;
			this.control = control;
			this.boostnoy = boostnoy;
		}

		public String toString() {
			return name;
		}
	}

	private enum TakeOffMode {
		ALWAYS("Always", true, false, false),
		START("Start", false, true, false),
		NONE("None", false, false, false);

		private final String name;
		private final boolean always;
		private final boolean start;
		private final boolean none;

		private TakeOffMode(String name, boolean always, boolean start, boolean none) {
			this.name = name;
			this.start = start;
			this.always = always;
			this.none = none;
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
