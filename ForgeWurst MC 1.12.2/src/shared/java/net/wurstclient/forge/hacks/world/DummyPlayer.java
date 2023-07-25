/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.other.customs.entities.DummyEntity;

import java.util.UUID;

public final class DummyPlayer extends Hack {

	private DummyEntity dummyEntity;

	public DummyPlayer() {
		super("DummyPlayer", "A dummy player, For now the entity just follows you.");
		setCategory(Category.WORLD);
	}


	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		dummyEntity = new DummyEntity(mc.player.posX, mc.player.posY, mc.player.posZ, new GameProfile(UUID.randomUUID(), "Dummy"), true);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.world.removeEntity(dummyEntity);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		dummyEntity.moveTowards(mc.player.getPosition().add(0.5, -1, 0.5));
	}
}
