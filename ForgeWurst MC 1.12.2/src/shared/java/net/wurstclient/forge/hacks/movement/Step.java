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
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

	private final CheckboxSetting safeNCP =
			new CheckboxSetting("SafeNCP", "Only applies to NCP mode, It slows down \n" +
					"timer which helps bypass.",
					false);

	public Step() {
		super("Step", "Allows you to step up blocks instantly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(height);
		addSetting(safeNCP);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.player.stepHeight = 0.5F;
		setTickLength(50);
	}

	//
	// WURST 7 STEP
	//

	private boolean a;
	private boolean b;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (!(mode.getSelected() == Mode.NCP)) {
			if (mode.getSelected() == Mode.SIMPLE) {
				// simple mode
				mc.player.stepHeight = height.getValueF();
				return;
			}

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
		} else {

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
					if (safeNCP.isChecked()) {
						setTickLength(50 / 0.05f);
					}
					a = true;
					b = true;
				}
			} else {
				a = false;
				if (b) {
					if (safeNCP.isChecked()) {
						setTickLength(50);
					}
					b = false;
				}
			}
		}
	}

	private enum Mode {
		SIMPLE("Simple"),
		LEGIT("Legit"),
		NCP("NCP");

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

