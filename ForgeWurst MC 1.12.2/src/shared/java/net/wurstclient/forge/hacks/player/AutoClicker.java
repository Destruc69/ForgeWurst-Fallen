/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class AutoClicker extends Hack {
	public AutoClicker() {
		super("AutoClicker", "Interacts with the block/entity you are looking at.");
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
			BlockPos blockPos = mc.objectMouseOver.getBlockPos();

			if (mc.player.ticksExisted % 2 == 0) {
				mc.playerController.clickBlock(blockPos, mc.player.getHorizontalFacing());
				for (Entity entity : mc.world.loadedEntityList) {
					if (entity instanceof EntityItem) {
						if (entity.getPosition() == blockPos) {
							mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
						}
					}
				}
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}