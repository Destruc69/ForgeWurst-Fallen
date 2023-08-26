/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class AutoMount extends Hack {

	private final SliderSetting maxDistance =
			new SliderSetting("MaxDistance", 4, 1, 5, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting delay =
			new SliderSetting("Delay", 5, 2, 20, 1, SliderSetting.ValueDisplay.INTEGER);

	public AutoMount() {
		super("AutoMount", "Auto mounts rideable entities.");
		setCategory(Category.WORLD);
		addSetting(maxDistance);
		addSetting(delay);
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
		if (!mc.player.isRiding()) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity != null && entity != mc.player && entity.isEntityAlive()) {
					if (mc.player.getDistance(entity) <= maxDistance.getValueF()) {
						if (mc.player.ticksExisted % delay.getValueF() == 0) {
							mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
							mc.player.swingArm(EnumHand.MAIN_HAND);
						}
					}
				}
			}
		}
	}
}