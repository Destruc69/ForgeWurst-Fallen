/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.PlayerUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class BowAimBot extends Hack {
	public BowAimBot() {
		super("BowAimBot", "When using a bow it will aim at entitys.");
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
		for (Entity entity : mc.world.loadedEntityList) {
			if (PlayerUtils.CanSeeBlock(new BlockPos(entity.posX, entity.posY + 1, entity.posZ))) {
				if (entity != mc.player) {
					float[] rot = RotationUtils.getNeededRotations(new Vec3d(entity.lastTickPosX, entity.lastTickPosY + 1, entity.lastTickPosZ));
					if (mc.player.getHeldItemMainhand().getItem().equals(Items.BOW) && mc.player.isHandActive()) {
						mc.player.rotationYaw = rot[0];
						mc.player.rotationPitch = rot[1];
					}
				}
			}
		}
	}
}