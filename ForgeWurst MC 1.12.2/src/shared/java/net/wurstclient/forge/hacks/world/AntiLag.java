/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class AntiLag extends Hack {

	public AntiLag() {
		super("AntiLag", "Working out of the pocket anti-lag (this anti lag is really simple).");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private boolean a;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (isPlayerStill(mc.player)) {
			if (!a) {
				Runtime.getRuntime().gc();
				a = true;
			}
		} else {
			a = false;
		}
	}

	public static boolean isPlayerStill(EntityPlayer player) {
		// Check if yaw and pitch are not changing significantly
		float prevYaw = player.prevRotationYaw;
		float prevPitch = player.prevRotationPitch;
		float currYaw = player.rotationYaw;
		float currPitch = player.rotationPitch;

		float yawThreshold = 1.0f; // Adjust this threshold as needed
		float pitchThreshold = 1.0f; // Adjust this threshold as needed

		if (Math.abs(currYaw - prevYaw) > yawThreshold || Math.abs(currPitch - prevPitch) > pitchThreshold) {
			return false;
		}

		// Check if the player is not moving significantly
		double prevPosX = player.prevPosX;
		double prevPosY = player.prevPosY;
		double prevPosZ = player.prevPosZ;
		double currPosX = player.posX;
		double currPosY = player.posY;
		double currPosZ = player.posZ;

		double moveThreshold = 0.01; // Adjust this threshold as needed

		double distanceSq = (currPosX - prevPosX) * (currPosX - prevPosX) +
				(currPosY - prevPosY) * (currPosY - prevPosY) +
				(currPosZ - prevPosZ) * (currPosZ - prevPosZ);

		return !(distanceSq > moveThreshold * moveThreshold);
	}
}