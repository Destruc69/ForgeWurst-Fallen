/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;

public final class FastFall extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private final SliderSetting speed =
			new SliderSetting("Speed", 0.9, 0.1, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);


	public FastFall() {
		super("FastFall", "Fall fatser when you in the air.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(speed);
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
				mc.player.motionY = -speed.getValueF();
			}
		}
		if (mode.getSelected().ncp) {
			if (mc.player.fallDistance > 1) {
				if (!mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.7531999805211997D, mc.player.posZ)).getBlock().equals(Blocks.AIR))
					return;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.41999998688698D, mc.player.posZ, true));
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.7531999805211997D, mc.player.posZ, true));
				mc.player.setPosition(mc.player.posX, mc.player.posY - 0.7531999805211997D, mc.player.posZ);
			}
		}
	}

	private enum Mode {
		NORMAL("Normal", true, false),
		NCP("NCP", false, true);

		private final String name;
		private final boolean normal;
		private final boolean ncp;

		private Mode(String name, boolean normal, boolean ncp) {
			this.name = name;
			this.normal = normal;
			this.ncp = ncp;
		}

		public String toString() {
			return name;
		}
	}
}