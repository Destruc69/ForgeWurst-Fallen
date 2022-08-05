/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Rotations;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.RotationUtils;

public final class Criticals extends Hack {
	private final SliderSetting critstrength =
			new SliderSetting("Crits-Strength", "How strong are the crits?", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Criticals() {
		super("Criticals", "Get critical hits.");
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
			if (entity.hitByEntity(mc.player)) {
				for (int x = 0; x < critstrength.getValueF(); x ++) {
					doCrits();
				}
			}
		}
	}

	public static void doCrits() {
		//0.0625 , 17.64e-8
		double off = 0.0626;
		double x = mc.player.posX;
		double y = mc.player.posY;
		double z = mc.player.posZ;
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + off, z, false));
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y + off + 0.00000000001, z, false));
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, false));
	}
}