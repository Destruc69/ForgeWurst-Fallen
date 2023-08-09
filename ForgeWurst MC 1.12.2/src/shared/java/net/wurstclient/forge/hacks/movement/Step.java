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
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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


	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
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
	}

	private enum Mode
	{
		SIMPLE("Simple"),
		LEGIT("Legit");

		private final String name;

		private Mode(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}