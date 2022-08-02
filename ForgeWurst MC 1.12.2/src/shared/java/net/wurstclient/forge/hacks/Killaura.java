/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.TimerUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;

public final class Killaura extends Hack {

	private final SliderSetting rotStrength =
			new SliderSetting("RotationStrength", "How strong are the rotations?", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	double delay;

	public Killaura() {
		super("Killaura", "Automatically attacks entities around you.");
		setCategory(Category.COMBAT);
		addSetting(rotStrength);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		TimerUtils.reset();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (TimerUtils.getTimePassed() <= 1000) {
			delay = 2;
		} else if (TimerUtils.getTimePassed() >= 1000 && TimerUtils.getTimePassed() <= 2000) {
			delay = 3;
		} else if (TimerUtils.getTimePassed() >= 2000 && TimerUtils.getTimePassed() <= 3000) {
			delay = 4;
		} else if (TimerUtils.getTimePassed() > 3000) {
			TimerUtils.reset();
		}
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity != mc.player && !entity.isDead && entity != null) {
					if (mc.player.getDistance(entity) < 3) {
						for (int x = 0; x < rotStrength.getValueF(); x ++) {
							RotationUtils.faceVectorPacket(new Vec3d(entity.lastTickPosX, entity.lastTickPosY + Math.random() * 1, entity.lastTickPosZ));
						}
						if (mc.player.ticksExisted % delay == 0) {
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