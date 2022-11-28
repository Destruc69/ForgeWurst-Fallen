/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.lang.reflect.Field;

public final class Step extends Hack {
	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NCP);

	public Step() {
		super("Step", "thank you cookie client by beloved.");
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
		if (mode.getSelected().ncp) {
			if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
				AxisAlignedBB box = mc.player.getEntityBoundingBox().offset(0.0, 0.05, 0.0).grow(0.05);
				if (!mc.world.getCollisionBoxes(mc.player, box.offset(0.0, 1.0, 0.0)).isEmpty()) {
					return;
				}

				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, true));
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, true));
				mc.player.setPosition(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ);
			}
		} else if (mode.getSelected().aac) {
			if (!mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY = -999;
			}
		} else if (mode.getSelected().basic) {
			if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.fallDistance == 0.0f && !mc.player.isOnLadder() && !mc.player.movementInput.jump) {
				AxisAlignedBB box = mc.player.getEntityBoundingBox().offset(0.0, 0.05, 0.0).grow(0.05);
				if (!mc.world.getCollisionBoxes(mc.player, box.offset(0.0, 1.0, 0.0)).isEmpty())
					return;

				mc.player.jump();
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
			}
		}
	}
	private enum Mode {
		NCP("NCP", true, false, false),
		AAC("AAC", false, true, false),
		BASIC("Basic", false, false, true);

		private final String name;
		private final boolean ncp;
		private final boolean aac;
		private final boolean basic;

		private Mode(String name, boolean ncp, boolean aac, boolean basic) {
			this.name = name;
			this.aac = aac;
			this.ncp = ncp;
			this.basic = basic;
		}

		public String toString() {
			return name;
		}
	}
}