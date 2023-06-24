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
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
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
import net.wurstclient.forge.utils.MathUtils;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.CONTROL);

	private final SliderSetting upSpeed =
			new SliderSetting("Up=Speed", 1, 0.05, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("Down-Speed", 1, 0.05, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("Base-Speed", 1, 0.05, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting boostNoY =
			new CheckboxSetting("BoostNoY", "Excludes y boosting on Boost mode.",
					false);

	private final CheckboxSetting antiFireworkLag =
			new CheckboxSetting("AntiFireworkLag", "Helps lag with fireworks on servers anti-cheats.",
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
			}
		} else {
			if (mc.player.motionY < 0) {
				if (mc.player.ticksExisted % 10 == 0) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
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
		CONTROL("Control", true, false, false, false),
		BOOST("Boost", false, true, false, false),
		FIREWORK("Firework", false, false, true, false),
		ROCKET("Rocket", false, false, false, true),
		NONE("None", false, false, false, false);

		private final String name;
		private final boolean control;
		private final boolean boost;
		private final boolean firework;
		private final boolean rocket;

		private Mode(String name, boolean control, boolean boost, boolean firework, boolean rocket) {
			this.name = name;
			this.control = control;
			this.firework = firework;
			this.boost = boost;
			this.rocket = rocket;
		}

		public String toString() {
			return name;
		}
	}
}