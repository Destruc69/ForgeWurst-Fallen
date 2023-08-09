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
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.CONTROL);

	private final SliderSetting upSpeed =
			new SliderSetting("Up-Speed", 1, 0.01, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("Down-Speed", 1, 0.01, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("Base-Speed", 1, 0.01, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting boostNoY =
			new CheckboxSetting("BoostNoY", "Excludes y boosting on Boost mode.",
					false);

	private final CheckboxSetting antiFireworkLag =
			new CheckboxSetting("AntiFireworkLag", "Helps lag with fireworks on servers anti-cheats.",
					false);

	private final CheckboxSetting autoStart =
			new CheckboxSetting("AutoStart", "Opens the elytra for you automatically.",
					false);

	public ElytraFlight()
	{
		super("ElytraFlight", "Fly with an elytra.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(downSpeed);
		addSetting(baseSpeed);
		addSetting(boostNoY);
		addSetting(antiFireworkLag);
		addSetting(autoStart);
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
		if (mc.player.isElytraFlying()) {
			if (mode.getSelected().boost) {
				boostEF();
			} else if (mode.getSelected().firework) {
				firewordEF();
			} else if (mode.getSelected().control) {
				controlEF();
			} else if (mode.getSelected().rocket) {
				rocketEF();
			} else if (mode.getSelected().vanillaplus) {
				vanillaPlusEF();
			}
		} else {
			if (autoStart.isChecked()) {
				if (mc.player.motionY < 0) {
					if (mc.player.ticksExisted % 10 == 0) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
					}
				}
			}
		}

		if (antiFireworkLag.isChecked()) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityFireworkRocket) {
					if (entity.ticksExisted > 0) {
						mc.world.removeEntity(entity);
					}
				}
			}
		}
	}

	private void vanillaPlusEF() {
		double[] spd = MathUtils.directionSpeed(baseSpeed.getValueF());

		if (mc.gameSettings.keyBindForward.isKeyDown()) {
			if (mc.player.motionY > -0.5D) {
				mc.player.fallDistance = 1.0F;
			}

			Vec3d vec3d = mc.player.getLookVec();
			float f = mc.player.rotationPitch * 0.017453292F;
			double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
			double d8 = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
			double d1 = vec3d.lengthVector();
			float f4 = MathHelper.cos(f);
			f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
			mc.player.motionY += -0.08D + (double) f4 * 0.06D;

			if (mc.player.motionY < 0.0D && d6 > 0.0D) {
				double d2 = mc.player.motionY * -0.1D * (double) f4;
				mc.player.motionY += d2;
				mc.player.motionX += vec3d.x * d2 / d6 + spd[0];
				mc.player.motionZ += vec3d.z * d2 / d6 + spd[1];
			}

			if (f < 0.0F) {
				double d10 = d8 * (double) (-MathHelper.sin(f)) * 0.04D;
				mc.player.motionY += d10 * 3.2D;
				mc.player.motionX -= vec3d.x * d10 / d6 + spd[0];
				mc.player.motionZ -= vec3d.z * d10 / d6 + spd[1];
			}

			if (d6 > 0.0D) {
				mc.player.motionX += (vec3d.x / d6 * d8 - mc.player.motionX + spd[0]) * 0.1D;
				mc.player.motionZ += (vec3d.z / d6 * d8 - mc.player.motionZ + spd[1]) * 0.1D;
			}

			mc.player.motionX *= 0.9900000095367432D + spd[0];
			mc.player.motionY *= 0.9800000190734863D;
			mc.player.motionZ *= 0.9900000095367432D + spd[1];
			mc.player.move(MoverType.SELF, mc.player.motionX + spd[0], mc.player.motionY, mc.player.motionZ + spd[1]);
		}
	}

	private void rocketEF() {
		for (int a = 0; a < mc.player.inventory.getSizeInventory(); a ++) {
			if (mc.player.inventory.getStackInSlot(a).getItem().equals(Items.FIREWORKS)) {
				mc.player.inventory.currentItem = a;
				mc.playerController.updateController();
				mc.player.getHeldItem(EnumHand.MAIN_HAND);
			}
		}

		if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.FIREWORKS)) {
			mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}

	private void controlEF() {
		double[] spd = MathUtils.directionSpeed(baseSpeed.getValueF() - Math.random() * 0.005);
		mc.player.motionX = spd[0];
		mc.player.motionZ = spd[1];

		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY = +upSpeed.getValue();
		} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = -downSpeed.getValue();
		} else {
			mc.player.motionY = 0.032;
		}
	}

	private void boostEF() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			if (!boostNoY.isChecked()) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
		}
	}

	private void firewordEF() {
		for (int a = 0; a < mc.player.inventory.getSizeInventory(); a ++) {
			if (mc.player.inventory.getStackInSlot(a).getItem().equals(Items.FIREWORKS)) {
				mc.player.inventory.currentItem = a;
				mc.playerController.updateController();
				mc.player.getHeldItem(EnumHand.MAIN_HAND);
			}
		}
		for (int a = -1; a < 0; a ++) {
			if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.FIREWORKS)) {
				if (mc.player.isElytraFlying()) {
					if (mc.player.motionX < 0.4 && mc.player.motionX > -0.4 && mc.player.motionZ < 0.4 && mc.player.motionZ > -0.4 || !mc.world.getBlockState(mc.player.getPosition().add(0, -a, 0)).getBlock().equals(Blocks.AIR)) {
						if (mc.player.ticksExisted % 20 == 0) {
							mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
							mc.player.swingArm(EnumHand.MAIN_HAND);
							mc.playerController.updateController();
						}
					}
				}
			} else {
				if (mc.player.ticksExisted % 20 == 0) {
					ChatUtils.error("No fireworks in hotbar!");
				}
			}
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		if (mode.getSelected().control) {
			if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Rotation) {
					event.setCanceled(true);
				}
			}
		}
	}

	private enum Mode {
		CONTROL("Control", true, false, false, false, false),
		BOOST("Boost", false, true, false, false, false),
		FIREWORK("Firework", false, false, true, false, false),
		ROCKET("Rocket", false, false, false, true, false),
		NONE("None", false, false, false, false, false),
		VANILLAPLUS("VanillaPlus", false, false, false, false, true),
		Ignore("ignore", false, false, false, false, false);

		private final String name;
		private final boolean control;
		private final boolean boost;
		private final boolean firework;
		private final boolean rocket;
		private final boolean vanillaplus;

		private Mode(String name, boolean control, boolean boost, boolean firework, boolean rocket, boolean vanillaplus) {
			this.name = name;
			this.control = control;
			this.firework = firework;
			this.boost = boost;
			this.rocket = rocket;
			this.vanillaplus = vanillaplus;
		}

		public String toString() {
			return name;
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