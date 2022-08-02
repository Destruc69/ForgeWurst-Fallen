/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;

import java.util.Objects;

public final class EntitySpeed extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", 2, 0.5, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);


	public EntitySpeed() {
		super("EntitySpeed", "Move faster with Entitys/Ridables.");
		setCategory(Category.MOVEMENT);
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
		try {
			Objects.requireNonNull(mc.player.getRidingEntity()).rotationYaw = mc.player.rotationYaw;
			double[] dir = MathUtils.directionSpeed(speed.getValueF());
			Objects.requireNonNull(mc.player.getRidingEntity().motionX = dir[0]);
			Objects.requireNonNull(mc.player.getRidingEntity().motionZ = dir[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}