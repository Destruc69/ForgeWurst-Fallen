/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class Killaura extends Hack {

	public Killaura() {
		super("Killaura", "Automatically attacks entities around you.");
		setCategory(Category.COMBAT);
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
			for (Entity entity : mc.world.loadedEntityList) {
				assert entity != null;
				if (entity != mc.player) {
					if (mc.player.getDistance(entity) <= 3) {
						if (mc.player.ticksExisted % 5 == 0) {
							mc.playerController.attackEntity(mc.player, entity);
							mc.player.swingArm(EnumHand.MAIN_HAND);
						}
					}
				}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}