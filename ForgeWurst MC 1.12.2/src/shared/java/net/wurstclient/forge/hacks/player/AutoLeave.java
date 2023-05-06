/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;

public final class AutoLeave extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.HURT);

	public AutoLeave() {
		super("AutoLeave", "Leaves the game once a condition is true.");
		setCategory(Category.PLAYER);
		addSetting(mode);
	}

	private enum Mode {
		HURT("Hurt", true, false),
		PLAYERENTERED("PlayerEntered", false, true);

		private final String name;
		private final boolean hurt;
		private final boolean playerentered;

		private Mode(String name, boolean hurt, boolean playerentered) {
			this.name = name;
			this.hurt = hurt;
			this.playerentered = playerentered;
		}

		public String toString() {
			return name;
		}
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
		try {
			if (mode.getSelected().hurt) {
				if (mc.player.hurtTime > 0) {
					mc.player.connection.onDisconnect(new TextComponentString("[AUTOLEAVE] You were hurt, we left."));
				}
			} else if (mode.getSelected().playerentered) {
				for (Entity entity : mc.world.loadedEntityList) {
					if (entity instanceof EntityPlayer && entity != mc.player) {
						mc.player.connection.onDisconnect(new TextComponentString("[AUTOLEAVE] Player entered your visibility, we left.."));
					}
				}
			}
		} catch (Exception ignored) {
		}
	}
}