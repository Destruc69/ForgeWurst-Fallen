/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class Aimbot extends Hack {

	public Aimbot() {
		super("Aimbot", "Sends packets to look at entitys..");
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
	public void onPacketIn(WUpdateEvent event) {
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				assert entity != null;
				if (entity != mc.player) {
					if (mc.player.getDistance(entity) <= 4) {
						RotationUtils.faceVectorPacket(new Vec3d(entity.lastTickPosX, entity.lastTickPosY + Math.random() * 2 - Math.random() * 1, entity.lastTickPosZ));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}