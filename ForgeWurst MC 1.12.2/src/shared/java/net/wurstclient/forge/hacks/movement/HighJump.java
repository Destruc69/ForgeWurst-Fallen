/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
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
import net.wurstclient.forge.utils.KeyBindingUtils;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public final class HighJump extends Hack {

	private ArrayList<Packet> packetArrayList;

	private final SliderSetting speed =
			new SliderSetting("Speed", "Greater the speed means higher the jump", 0.2, 0.1, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Blink bypass for HighJump.",
					false);

	public HighJump() {
		super("HighJump", "Jump higher.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(bypass);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		packetArrayList = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 0.1, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
			mc.player.motionY = speed.getValue();
		}

		if (!mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 0.1, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
			if (packetArrayList.size() > 0) {
				for (Packet packet : packetArrayList) {
					mc.player.connection.sendPacket(packet);
				}
				packetArrayList.clear();
			}
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		if (bypass.isChecked()) {
			if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation || event.getPacket() instanceof CPacketPlayer.Position) {
				if (mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 0.1, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
					packetArrayList.add(event.getPacket());
					event.setCanceled(true);
				}
			}
		}
	}
}