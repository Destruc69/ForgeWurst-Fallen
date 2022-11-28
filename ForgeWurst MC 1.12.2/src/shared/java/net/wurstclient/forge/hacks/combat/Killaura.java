/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.TimerUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;

public final class Killaura extends Hack {

	public static float yawww;
	public static float pitch;

	public Killaura() {
		super("Killaura", "Automatically attacks entities around you.");
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
		assert event != null;
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				assert entity != null;
				if (entity != mc.player) {
					if (mc.player.getDistance(entity) < 3) {
						if (entity.isEntityAlive() && entity != null) {
							mc.playerController.attackEntity(mc.player, entity);
							mc.player.swingArm(EnumHand.MAIN_HAND);
						}
						float[] rot = RotationUtils.getNeededRotations(new Vec3d(entity.lastTickPosX, entity.lastTickPosY + Math.random() * 1, entity.lastTickPosZ));
						yawww = rot[0];
						pitch = rot[1];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		assert event != null;
		try {
			if (event.getPacket() instanceof CPacketPlayer.Rotation) {
				event.setCanceled(true);
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yawww, pitch, mc.player.onGround));
			}
			if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
				event.setCanceled(true);
				mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, yawww, pitch, mc.player.onGround));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}