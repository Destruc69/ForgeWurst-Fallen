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
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

import java.util.Objects;

public final class Hitboxes extends Hack {

	private static final SliderSetting grow = new SliderSetting("AddAmount", "how much we add to the bounding boxes", 5.0D, 1.0D, 80.0D, 1.0D, SliderSetting.ValueDisplay.DECIMAL);


	public Hitboxes() {
		super("HitBox", "Grow bounding boxes.");
		setCategory(Category.COMBAT);
		addSetting(grow);
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
	public static void onUpdate(WUpdateEvent event) {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity != mc.player) {
				entity.getEntityBoundingBox().expand(grow.getValueI(), grow.getValueI(), grow.getValueI());
				Objects.requireNonNull(entity.getCollisionBoundingBox()).expand(grow.getValueI(), grow.getValueF(), grow.getValueI());
				entity.getRenderBoundingBox().expand(grow.getValueI(), grow.getValueF(), grow.getValueI());
			}
		}
	}
}