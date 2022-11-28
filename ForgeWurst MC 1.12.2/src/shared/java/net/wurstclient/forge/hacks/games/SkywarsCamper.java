/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.games;

import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class SkywarsCamper extends Hack {

	public static double aimZ;
	public static double aimY;
	public static double aimX;
	public static double aimYaw;
	public static double aimPitch;

	public SkywarsCamper() {
		super("SkywarsCamper", "Makes you impossible to find.");
		setCategory(Category.GAMES);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			aimY = mc.player.posY + 40;
			aimX = mc.player.posX;
			aimZ = mc.player.posZ;
			aimYaw = mc.player.rotationYaw;
			aimPitch = mc.player.rotationPitch;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			mc.player.setPosition(aimX, aimY, aimZ);
			mc.player.rotationYaw = (float) aimYaw;
			mc.player.rotationPitch = (float) aimPitch;
			mc.player.motionX = 0;
			mc.player.motionY = 0;
			mc.player.motionZ = 0;
			mc.player.setVelocity(0, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof CPacketPlayer.Position) {
				event.setCanceled(true);
				mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(aimX, aimY, aimZ, (float) aimYaw, (float) aimPitch, true));
			}
			if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
				event.setCanceled(true);
				mc.player.connection.sendPacket(new CPacketPlayer.Position(aimX, aimY, aimZ, true));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}