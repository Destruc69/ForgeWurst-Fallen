/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class ReachPlus extends Hack {

	private final SliderSetting dist =
			new SliderSetting("Distance", "How far should the distance be?", 3, 3, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	public ReachPlus() {
		super("ReachPlus", "Extend your reach distance.");
		setCategory(Category.COMBAT);
		addSetting(dist);
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
	public void update(WUpdateEvent event) {
		mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(dist.getValue());
	}
}