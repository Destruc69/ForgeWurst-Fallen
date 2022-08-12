/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
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
		AAC("AAC", true, false, false),
		MINEPLEX("Mineplex", false, true, false),
		BLOCKMC("BlocksMC", false, false, true);

		private final String name;
		private final boolean aac;
		private final boolean mineplex;
		private final boolean blocksmc;

		private Mode(String name, boolean aac, boolean mineplex, boolean blocksmc) {
			this.name = name;
			this.aac = aac;
			this.mineplex = mineplex;
			this.blocksmc = blocksmc;
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
		if (mode.getSelected().mineplex) {
			mc.player.motionY += 0.0132099999999999999999999999999;
			mc.player.jumpMovementFactor = 0.08f;
			if (mc.player.fallDistance != 0.0f) {
				mc.player.motionY += 0.037;
			}
		}
		if (mode.getSelected().blocksmc) {
			mc.player.jumpMovementFactor = 0.1f;
			mc.player.motionY += 0.0132;
		}
	}
}