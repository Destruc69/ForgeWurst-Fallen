/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

import java.util.Objects;

public final class Test extends Hack {

	public Test() {
		super("ClientSideFixer", "Max build height, ect [client sided]");
		setCategory(Category.PLAYER);
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
			Objects.requireNonNull(mc.player.getServer()).setAllowFlight(true);
			mc.player.getServer().setAllowPvp(true);
			mc.player.getServer().setBuildLimit(999);
			mc.player.getServer().setCanSpawnAnimals(true);
			mc.player.getServer().setCanSpawnNPCs(true);
			mc.player.getServer().setForceGamemode(false);
			mc.player.getServer().setPlayerIdleTimeout(0);
			mc.player.getServer().setServerOwner(mc.player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}