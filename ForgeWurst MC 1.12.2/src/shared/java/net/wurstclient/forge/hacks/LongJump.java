/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;

import java.lang.reflect.Field;

public final class LongJump extends Hack {

	public static double x;
	public static double y;
	public static double z;

	public static boolean teleported;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.AAC);

	private enum Mode {
		AAC("AAC1", true, false, false, false, false, false, false, false),
		AAC2("AAC2", false, false, false, true, false, false, false, false),
		AAC3("AAC3", false, false, false, false, true, false, false, false),
		MINEPLEX("Mineplex1", false, true, false, false, false, false, false, false),
		MINEPLEX2("Mineplex2", false, false, false, false, false, true, false, false),
		MINEPLEX3("Mineplex3", false, false, false, false, false, false, true, false),
		BLOCKMC("BlocksMC", false, false, true, false, false, false, false, false),
		NCP("NCP", false, false, false, false, false, false, false, true);

		private final String name;
		private final boolean aac;
		private final boolean aac2;
		private final boolean aac3;
		private final boolean mineplex;
		private final boolean mineplex2;
		private final boolean blocksmc;
		private final boolean mineplex3;
		private final boolean ncp;

		private Mode(String name, boolean aac, boolean mineplex, boolean blocksmc, boolean aac2, boolean aac3, boolean mineplex2, boolean mineplex3, boolean ncp) {
			this.name = name;
			this.aac = aac;
			this.mineplex = mineplex;
			this.blocksmc = blocksmc;
			this.mineplex2 = mineplex2;
			this.aac2 = aac2;
			this.aac3 = aac3;
			this.mineplex3 = mineplex3;
			this.ncp = ncp;
		}

		public String toString() {
			return name;
		}
	}

	public LongJump() {
		super("LongJump", "Jump far");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		x = mc.player.posX;
		y = mc.player.posY;
		z = mc.player.posZ;
		teleported = false;
	}
	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().aac) {
			if (mc.player.fallDistance > 0.5 && !teleported) {
				if (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH)) {
					z = z - 5;
				}
				if (mc.player.getHorizontalFacing().equals(EnumFacing.EAST)) {
					x = x + 5;
				}
				if (mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH)) {
					z = z + 5;
				}
				if (mc.player.getHorizontalFacing().equals(EnumFacing.WEST)) {
					x = x - 5;
				}
				mc.player.setPosition(x, y, z);
				teleported = true;
			}
		}
		if (mode.getSelected().ncp) {
			if (mc.player.hurtTime > 0) {
				mc.player.jump();
			} else {

			}
		}
		if (mode.getSelected().aac3) {
			mc.player.motionY += 0.05999;
		}
		if (mode.getSelected().aac2) {
			mc.player.motionY += 0.0132099999999999999999999999999;
			mc.player.jumpMovementFactor = 0.08f;
		}
		if (mode.getSelected().mineplex) {
			mc.player.motionY += 0.0132099999999999999999999999999;
			mc.player.jumpMovementFactor = 0.08f;
		}
		if (mode.getSelected().mineplex2) {
			mc.player.jumpMovementFactor = 0.1f;
			if (mc.player.fallDistance > 1.5f) {
				mc.player.jumpMovementFactor = 0f;
				mc.player.motionY = (-10f);
			}
			if (mc.player.fallDistance != 0.0f) {
				mc.player.motionY += 0.037;
			}
		}
		if (mode.getSelected().mineplex3) {
			mc.player.motionY += 0.0132099999999999999999999999999;
			mc.player.jumpMovementFactor = 0.08f;
		}
		if (mode.getSelected().blocksmc) {
			mc.player.jumpMovementFactor = 0.1f;
			mc.player.motionY += 0.0132;
		}
	}
}