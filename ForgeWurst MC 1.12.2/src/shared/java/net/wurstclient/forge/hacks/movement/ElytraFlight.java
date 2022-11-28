/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;

import javax.sound.sampled.Clip;
import java.lang.reflect.Field;
import java.util.ArrayList;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", "Modes for ElytraFlight", Mode.values(), Mode.BOOST);

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", "Speed for going Up", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("BaseSpeed", "Speed for going forwards, left, right and back", 0.4, 0, 2, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", "Speed for going down", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting takeoff =
			new CheckboxSetting("AutoTakeOff", "Sends packet to start elytra flying",
					false);

	private final SliderSetting autoy =
			new SliderSetting("AutoY", "Y Value for Auto Pilot", 150, 0, 300, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting autoboolean =
			new CheckboxSetting("AutoPilot", "Maintain a y value automatically",
					false);

	private final CheckboxSetting render =
			new CheckboxSetting("Renders", "Cool rendering for elytra flying",
					false);

	public ElytraFlight() {
		super("ElytraFlight", "Fly with elytras.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(takeoff);
		addSetting(autoy);
		addSetting(autoboolean);
		addSetting(render);
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
	public void update(WUpdateEvent event) {
		if (mc.player.isElytraFlying()) {
			if (mode.getSelected().boostnoy || mode.getSelected().boost) {
				boostElytraFlight();
			} else if (mode.getSelected().control) {
				controlElytraFlight();
			}
			if (autoboolean.isChecked()) {
				autoPilot();
			}
		} else {
			if (takeoff.isChecked()) {
				if (mc.player.ticksExisted % 4 == 0) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
				}
			}
		}
	}

	public void autoPilot() {
		if (!mc.player.collidedVertically && !mc.player.collidedHorizontally) {
			double yAim = autoy.getValueF();
			if (mc.player.posY <= yAim) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
			} else {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
			}
			if (Math.round(mc.player.posY) >= Math.round(yAim)) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
			} else {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
			}
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
			setEnabled(false);
			try {
				ChatUtils.warning("[EF-AUTO-PILOT] Collided, please take over,");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void boostElytraFlight() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			if (!mode.getSelected().boostnoy) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
		}
		if (!mode.getSelected().boostnoy) {
			if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
				Minecraft.getMinecraft().player.motionY += upSpeed.getValueF();
			if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
				Minecraft.getMinecraft().player.motionY -= downSpeed.getValueF();
		}
	}

	public void controlElytraFlight() {
		boolean keysActive = mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown();
		boolean jumpActive = mc.gameSettings.keyBindJump.isKeyDown();
		boolean sneakActive = mc.gameSettings.keyBindSneak.isKeyDown();
		if (keysActive) {
			double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
			mc.player.motionX = dir[0];
			mc.player.motionZ = dir[1];
			if (!jumpActive && !sneakActive) {
				mc.player.motionY = 0;
			}
		}
		if (!jumpActive && !sneakActive && !keysActive) {
			mc.player.motionY = 0;
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
		if (jumpActive) {
			mc.player.motionY += upSpeed.getValueF();
		}
		if (sneakActive) {
			mc.player.motionY -= downSpeed.getValueF();
		}
	}


	private enum Mode {
		BOOST("Boost", true, false, false),
		CONTROL("Control", false, true, false),
		BOOSTNOY("Boost-NO-Y", false, false, true);

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

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (render.isChecked()) {
			for (int y = (int) mc.player.posY; y > 0; y--) {
				BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - y, mc.player.posZ);
				if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
					FallenRenderUtils.renderPosFilled(blockPos, event.getPartialTicks(), 0, 0, 100, 0.5F);
				}
			}
			if (autoboolean.isChecked()) {
				FallenRenderUtils.renderPosFilled(new BlockPos(mc.player.posX, autoy.getValueF(), mc.player.posZ), event.getPartialTicks(), 0, 0, 100, 0.5F);
			}
		}
	}
}