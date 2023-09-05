/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.RotationUtils;

public final class Killaura extends Hack {

	private final SliderSetting distance =
			new SliderSetting("Distance", "How far should the distance be?", 4, 1, 5, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Killaura() {
		super("Killaura", "Automatically attacks entities around you.");
		setCategory(Category.COMBAT);
		addSetting(distance);
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
		Entity entity = findClosestEntity();
		if (entity != null) {
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(entity.lastTickPosX + 0.5, entity.lastTickPosY + 0.5, entity.lastTickPosZ + 0.5));
			if (mc.player.ticksExisted % 10 == 0) {
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
				mc.playerController.attackEntity(mc.player, entity);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
		}
	}

	private EntityLivingBase findClosestEntity() {
		EntityLivingBase closestEntity = null;
		double closestDistance = Double.MAX_VALUE;

		for (Entity entity : mc.player.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityLivingBase && entity != mc.player) {
				double distance = mc.player.getDistanceSq(entity.posX, entity.posY, entity.posZ);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEntity = (EntityLivingBase) entity;
				}
			}
		}
		assert closestEntity != null;
		if (mc.player.getDistance(closestEntity) < distance.getValueF()) {
			return closestEntity;
		} else {
			return null;
		}
	}
}