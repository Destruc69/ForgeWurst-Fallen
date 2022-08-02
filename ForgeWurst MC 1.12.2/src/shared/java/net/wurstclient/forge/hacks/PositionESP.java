/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class PositionESP extends Hack {
	public PositionESP() {
		super("OutlineESP", "Renders entitys outlines for pitch and yaw");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.getRenderManager().setDebugBoundingBox(false);
		mc.getRenderManager().setRenderShadow(false);
	}

	@SubscribeEvent
	public void onUpdate(RenderWorldLastEvent event) {
		for (Entity entity : mc.world.loadedEntityList) {
			mc.getRenderManager().setDebugBoundingBox(true);
			mc.getRenderManager().setRenderShadow(true);
		}
	}
}