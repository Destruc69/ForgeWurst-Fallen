/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.BlockUtils;

public final class GlideHack extends Hack {
	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private enum Mode {
		NORMAL("Normal", true, false, false),
		AAC("AAC", false, true, false),
		VULCAN("Vulcan", false, false, true);

		private final String name;
		private final boolean normal;
		private final boolean aac;
		private final boolean vulcan;

		private Mode(String name, boolean normal, boolean aac, boolean vulcan) {
			this.name = name;
			this.normal = normal;
			this.aac = aac;
			 this.vulcan = vulcan;
		}

		public String toString() {
			return name;
		}
	}

	public GlideHack() {
		super("Glide", "Makes you glide down slowly when falling.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
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
		if (mode.getSelected().normal) {
			if (mc.player.fallDistance > 1) {
				mc.player.motionY = -mc.player.motionY / -1.35;
			}
		}
		if (mode.getSelected().aac) {
			if (mc.player.motionY > 0) {
				mc.player.motionY = mc.player.motionY / 0.9800000190734863;
				mc.player.motionY += 0.03;
				mc.player.motionY *= 0.9800000190734863;
				mc.player.jumpMovementFactor = 0.03625f;
			}
		}
		if (mode.getSelected().vulcan) {
			if (mc.player.motionY <= -0.10) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.motionY = -0.1;
					mc.player.jumpMovementFactor = 0.0265f;
				} else {
					mc.player.motionY = -0.16;
					mc.player.jumpMovementFactor = 0.0265f;
				}
			}
		}
	}
}