/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class AntiLag extends Hack {

	public static Vec3d vec3d;

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

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			if (mc.player.ticksExisted % 1200 == 0) {
				mc.freeMemory();
			}

			if (Math.round(vec3d.x) != Math.round(mc.player.posX) ||
					Math.round(vec3d.y) != Math.round(mc.player.posY) ||
					Math.round(vec3d.z) != Math.round(mc.player.posZ)) {
				mc.player.setPosition(mc.player.prevPosX, mc.player.prevPosY, mc.player.prevPosZ);
				mc.ingameGUI.setOverlayMessage("[ANTI-LAG] Hang on, server position does not equal client position", true);
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				vec3d = new Vec3d(Math.round(sPacketPlayerPosLook.getX()), Math.round(sPacketPlayerPosLook.getY()), Math.round(sPacketPlayerPosLook.getZ()));
			}
		} catch (Exception ignored) {
		}
	}
}