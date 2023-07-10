/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.movement.AutoSprintHack;
import net.wurstclient.forge.settings.EnumSetting;

public final class Criticals extends Hack {

	private static final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.BYPASS);

	public Criticals() {
		super("Criticals", "Get critical hits.");
		setCategory(Category.COMBAT);
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
	public void onAttackEntity(AttackEntityEvent event) {
		mc.player.onCriticalHit(event.getTarget());
	}

	private enum Mode {
		BYPASS("Bypass", true, false),
		BASIC("Basic", false, true);

		private final String name;
		private final boolean basic;
		private final boolean bypass;

		private Mode(String name, boolean basic, boolean bypass) {
			this.name = name;
			this.basic = basic;
			this.bypass = bypass;
		}

		public String toString() {
			return name;
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (event.getPacket() instanceof CPacketUseEntity) {
			CPacketUseEntity cPacketUseEntity = (CPacketUseEntity) event.getPacket();

			if (cPacketUseEntity.getAction().equals(CPacketUseEntity.Action.ATTACK)) {
				if (cPacketUseEntity.getEntityFromWorld(mc.world) instanceof EntityLivingBase && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
					doCrits();
				}
			}

		}
	}

	public static void doCrits() {
		double off = 0.0626;
		double x = mc.player.posX;
		double y = mc.player.posY;
		double z = mc.player.posZ;
		if (mode.getSelected().bypass) {
			mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + off, z, false));
			mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + off + 0.00000000001, z, false));
			mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
		} else if (mode.getSelected().basic) {
			mc.player.connection.sendPacket(new CPacketPlayer(false));
		}
	}
}