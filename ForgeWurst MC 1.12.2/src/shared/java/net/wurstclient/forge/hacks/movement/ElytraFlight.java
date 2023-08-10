/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;

import java.lang.reflect.Field;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.CONTROL);

	private final SliderSetting upSpeed =
			new SliderSetting("Up-Speed", 1, 0, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("Base-Speed", 1, 0, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("Down-Speed", 1, 0, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting autoTakeOff =
			new CheckboxSetting("AutoTakeOff", "Takes off automatically.",
					false);

	private final SliderSetting lockPitch =
			new SliderSetting("LockPitch", 4, -10, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	public ElytraFlight()
	{
		super("ElytraFlight", "Fly with an elytra.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(autoTakeOff);
		addSetting(lockPitch);
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

	private boolean a;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.isElytraFlying()) {
			mc.player.rotationPitch = lockPitch.getValueF();
			if (mode.getSelected() == Mode.BOOST) {
				boostEF();
			} else if (mode.getSelected() == Mode.CONTROL) {
				controlEF();
			} else if (mode.getSelected() == Mode.TBTT) {
				tbttEF();
			}
			if (!a) {
				setTickLength(50);
			}
		} else {
			if (autoTakeOff.isChecked()) {
				if (mc.player.onGround) {
					mc.player.jump();
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
					a = true;
				} else if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.5, mc.player.posZ)).getBlock().equals(Blocks.AIR)) {
					if (mc.player.ticksExisted % 2 == 0) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
					}
					if (a) {
						setTickLength(50 / 0.05f);
						a = false;
					}
				}
			}
		}

		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityFireworkRocket) {
				if (entity.ticksExisted > 0) {
					mc.world.removeEntity(entity);
				}
			}
		}
	}

	private void boostEF() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			if (upSpeed.getValueF() > 0) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
		}
	}

	private void controlEF() {
		if (isKeyInputs()) {
			setSpeed(baseSpeed.getValueF());
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY = upSpeed.getValue();
		} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = -downSpeed.getValue();
		} else {
			mc.player.motionY = 0;
		}
	}

	private void tbttEF() {
		if (mc.gameSettings.keyBindForward.isKeyDown()) {
			double yaw = Math.toRadians(ElytraFlight.mc.player.rotationYaw);
			mc.player.motionX -= (ElytraFlight.mc.player.movementInput.moveForward * Math.sin(yaw) * 0.04);
			mc.player.motionZ += (ElytraFlight.mc.player.movementInput.moveForward * Math.cos(yaw) * 0.04);
			mc.player.motionY = -1.01E-4;
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		if (mode.getSelected() == Mode.CONTROL && !isKeyInputs() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
			if (event.getPacket() instanceof CPacketPlayer ||
			event.getPacket() instanceof CPacketPlayer.Rotation ||
			event.getPacket() instanceof CPacketPlayer.PositionRotation ||
			event.getPacket() instanceof CPacketPlayer.Position) {
				event.setCanceled(true);
			}
		}
	}

	private enum Mode {
		CONTROL("Control"),
		BOOST("Boost"),
		TBTT("2b2t");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private void setSpeed(final double speed) {
		Minecraft.getMinecraft().player.motionX = -MathHelper.sin(getDirection()) * speed;
		Minecraft.getMinecraft().player.motionZ = MathHelper.cos(getDirection()) * speed;
	}

	private void setMoveSpeed(double speed) {
		double forward = mc.player.movementInput.moveForward;
		double strafe = mc.player.movementInput.moveStrafe;
		float yaw = mc.player.rotationYaw;

		if (forward == 0.0 && strafe == 0.0) {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0) {
					yaw += (float) (forward > 0.0 ? -45 : 45);
				} else if (strafe < 0.0) {
					yaw += (float) (forward > 0.0 ? 45 : -45);
				}
				strafe = 0.0;
				if (forward > 0.0) {
					forward = 1.0;
				} else if (forward < 0.0) {
					forward = -1.0;
				}
			}

			double x = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
			double z = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));

			mc.player.motionX = x;
			mc.player.motionZ = z;
		}
	}

	private double[] forwardStrafeYaw(double forward, double strafe, double yaw) {
		double[] result = new double[]{forward, strafe, yaw};

		if (forward != 0.0) {
			if (strafe > 0.0) {
				result[2] = result[2] + (forward > 0.0 ? -45 : 45);
			} else if (strafe < 0.0) {
				result[2] = result[2] + (forward > 0.0 ? 45 : -45);
			}

			result[1] = 0.0;

			if (forward > 0.0) {
				result[0] = 1.0;
			} else if (forward < 0.0) {
				result[0] = -1.0;
			}
		}

		return result;
	}

	private boolean isKeyInputs() {
		return Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown();
	}

	private float getDirection() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		final float forward = Minecraft.getMinecraft().player.moveForward;
		final float strafe = Minecraft.getMinecraft().player.moveStrafing;
		yaw += ((forward < 0.0f) ? 180 : 0);
		int i = (forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45);
		if (strafe < 0.0f) {
			yaw += i;
		}
		if (strafe > 0.0f) {
			yaw -= i;
		}
		return yaw * 0.017453292f;
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

	public void ignore() {
		if (mc.player.isElytraFlying()) {
			mc.player.motionX = 0;
			mc.player.motionY = 0.05;
			mc.player.motionZ = 0;
		} else {
			if (mc.player.motionY < 0) {
				if (mc.player.ticksExisted % 10 == 0) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
				}
			}
			if (mc.player.onGround) {
				mc.player.jump();
			}
		}
	}
}