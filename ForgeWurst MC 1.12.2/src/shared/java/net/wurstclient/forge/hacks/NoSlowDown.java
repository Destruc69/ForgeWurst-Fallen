/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.PlayerUtils;

import java.util.Objects;

public final class NoSlowDown extends Hack {
	public NoSlowDown() {
		super("NoSlowDown", "No time to slow down when eating");
		setCategory(Category.MOVEMENT);
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
	public void onUpdate(InputUpdateEvent event) {
		if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood) {
			event.getMovementInput().moveForward *= 5;
			event.getMovementInput().moveStrafe *= 5;
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, PlayerUtils.GetLocalPlayerPosFloored(), EnumFacing.DOWN));
			if (Objects.requireNonNull(mc.player.getServer()).getName().contains("2b2t")) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			}
		} else {
			if (Objects.requireNonNull(mc.player.getServer()).getName().contains("2b2t")) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			}
		}
	}
}