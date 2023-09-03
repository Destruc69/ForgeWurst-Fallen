/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public final class Step extends Hack {

	private final EnumSetting<Mode> mode = new EnumSetting<>("Mode",
			"\u00a7lSimple\u00a7r mode can step up multiple blocks (enables Height slider).\n"
					+ "\u00a7lLegit\u00a7r mode can bypass NoCheat+.",
			Mode.values(), Mode.LEGIT);

	private final SliderSetting height =
			new SliderSetting("Height", "Only works in \u00a7lSimple\u00a7r mode.",
					1, 1, 10, 1, SliderSetting.ValueDisplay.INTEGER);

	public Step() {
		super("Step", "Allows you to step up blocks instantly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(height);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.player.stepHeight = 0.5F;
	}

	//
	// WURST 7 STEP
	//

	private boolean a;
	private boolean b;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		// simple mode
		if (mode.getSelected() == Mode.SIMPLE) {
			mc.player.stepHeight = height.getValueF();
		} else if (mode.getSelected() == Mode.LEGIT) {

			// legit mode
			EntityPlayerSP player = mc.player;
			player.stepHeight = 0.5F;

			if (!player.onGround || player.isInWater() || player.isInLava()) {
				return;
			}

			if (player.moveForward == 0 && player.moveStrafing == 0) {
				return;
			}

			if (player.movementInput.jump) {
				return;
			}

			AxisAlignedBB box = player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

			if (!mc.world.getCollisionBoxes(player, box.offset(0, 1, 0)).isEmpty()) {
				return;
			}

			double stepHeight = Double.NEGATIVE_INFINITY;

			ArrayList<AxisAlignedBB> blockCollisions = new ArrayList<>(mc.world.getCollisionBoxes(player, box));

			for (AxisAlignedBB bb : blockCollisions) {
				if (bb.maxY > stepHeight) {
					stepHeight = bb.maxY;
				}
			}

			stepHeight = stepHeight - player.posY;

			if (stepHeight < 0 || stepHeight > 1) {
				return;
			}

			NetHandlerPlayClient netHandler = player.connection;

			netHandler.sendPacket(new CPacketPlayer.Position(player.posX, player.posY + 0.42 * stepHeight, player.posZ, player.onGround));
			netHandler.sendPacket(new CPacketPlayer.Position(player.posX, player.posY + 0.753 * stepHeight, player.posZ, player.onGround));

			player.setPosition(player.posX, player.posY + 1 * stepHeight, player.posZ);
		} else if (mode.getSelected() == Mode.NCP) {
			EntityPlayerSP player = mc.player;

			if (!player.onGround || player.isInWater() || player.isInLava()) {
				return;
			}

			if (player.moveForward == 0 && player.moveStrafing == 0) {
				return;
			}

			if (player.movementInput.jump) {
				return;
			}

			AxisAlignedBB box = player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

			double stepHeight = Double.NEGATIVE_INFINITY;

			ArrayList<AxisAlignedBB> blockCollisions = new ArrayList<>(mc.world.getCollisionBoxes(player, box));

			for (AxisAlignedBB bb : blockCollisions) {
				if (bb.maxY > stepHeight) {
					stepHeight = bb.maxY;
				}
			}

			stepHeight = stepHeight - player.posY;

			if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
				if (!a) {
					ncpStep(stepHeight);
					a = true;
				}
			} else {
				a = false;
			}
		} else if (mode.getSelected() == Mode.AAC) {
			EntityPlayerSP player = mc.player;

			if (!player.onGround || player.isInWater() || player.isInLava()) {
				return;
			}

			if (player.moveForward == 0 && player.moveStrafing == 0) {
				return;
			}

			if (player.movementInput.jump) {
				return;
			}

			AxisAlignedBB box = player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

			double stepHeight = Double.NEGATIVE_INFINITY;

			ArrayList<AxisAlignedBB> blockCollisions = new ArrayList<>(mc.world.getCollisionBoxes(player, box));

			for (AxisAlignedBB bb : blockCollisions) {
				if (bb.maxY > stepHeight) {
					stepHeight = bb.maxY;
				}
			}

			stepHeight = stepHeight - player.posY;

			if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
				if (!b) {
					aacStep(stepHeight);
					b = true;
				}
			} else {
				b = false;
			}
		}
	}

	private enum Mode {
		SIMPLE("Simple"),
		LEGIT("Legit"),
		NCP("NCP"),
		AAC("AAC");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private void ncpStep(double height) {
		List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
		double posX = mc.player.posX;
		double posZ = mc.player.posZ;
		double y = mc.player.posY;
		if (height < 1.1) {
			double first = 0.42;
			double second = 0.75;
			if (height != 1) {
				first *= height;
				second *= height;
				if (first > 0.425) {
					first = 0.425;
				}
				if (second > 0.78) {
					second = 0.78;
				}
				if (second < 0.49) {
					second = 0.49;
				}
			}
			if (first == 0.42) {
				first = 0.41999998688698;
			}
			mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + first, posZ, false));
			if (y + second < y + height) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + second, posZ, false));
			}
		} else if (height < 1.6) {
			for (double off : offset) {
				y += off;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y, posZ, false));
			}
		} else if (height < 2.1) {
			double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
			for (double off : heights) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
			}
		} else {
			double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
			for (double off : heights) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y + off, posZ, false));
			}
		}
		if (height <= 0) {
			mc.player.setPosition(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ);
		} else if (height > 0) {
			mc.player.setPosition(mc.player.posX, mc.player.posY + height - 0.58, mc.player.posZ);
		}
	}

	public void aacStep(double height){
		double posX = mc.player.posX; double posY = mc.player.posY; double posZ = mc.player.posZ;
		if (height > 0) {
			mc.player.setPosition(mc.player.posX, mc.player.posY + height - 0.58, mc.player.posZ);
		}
		if(height < 1.1){
			double first = 0.42;
			double second = 0.75;
			if(height > 1){
				first *= height;
				second *= height;
				if(first > 0.4349){
					first = 0.4349;
				}else if(first < 0.405){
					first = 0.405;
				}
			}
			mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, posY + first, posZ, false));
			if(posY+second < posY + height)
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, posY + second, posZ, false));
			return;
		}
		List<Double> offset = Arrays.asList(0.434999999999998,0.360899999999992,0.290241999999991,0.220997159999987,0.13786084000003104,0.055);
		double y = mc.player.posY;
		for(int i = 0; i < offset.size(); i++){
			double off = offset.get(i);
			y += off;
			if(y > mc.player.posY + height){
				double x = mc.player.posX; double z = mc.player.posZ;
				double forward = mc.player.movementInput.moveForward;
				double strafe = mc.player.movementInput.moveStrafe;
				float YAW = mc.player.rotationYaw;
				double speed = 0.3;
				if(forward != 0 && strafe != 0)
					speed -= 0.09;
				x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) *1;
				z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) *1;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(
						x, y,z, false));
				break;
			}
			if(i== offset.size() - 1){
				double x = mc.player.posX; double z = mc.player.posZ;
				double forward = mc.player.movementInput.moveForward;
				double strafe = mc.player.movementInput.moveStrafe;
				float YAW = mc.player.rotationYaw;
				double speed = 0.3;
				if(forward != 0 && strafe != 0)
					speed -= 0.09;
				x += (forward * speed * Math.cos(Math.toRadians(YAW + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(YAW + 90.0f))) *1;
				z += (forward * speed * Math.sin(Math.toRadians(YAW + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(YAW + 90.0f))) *1;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(
						x, y,z, false));
			}else{
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX, y, posZ, false));
			}
		}
	}
}

