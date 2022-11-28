/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.item.ItemFood;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class NoSlowDown extends Hack {

	private final CheckboxSetting ncp =
			new CheckboxSetting("NCP", "Bypass NCP",
					false);

	private final CheckboxSetting tbtb =
			new CheckboxSetting("2b2t", "Bypass 2b2t",
					false);

	public NoSlowDown() {
		super("NoSlowDown", "No time to slow down when eating");
		setCategory(Category.MOVEMENT);
		addSetting(ncp);
		addSetting(tbtb);
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
			if (ncp.isChecked()) {
				ncpPacket();
			}
			if (tbtb.isChecked()) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			}
		} else {
			if (tbtb.isChecked()) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			}
		}
	}
	public void ncpPacket() {
		NoSlowDown.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(NoSlowDown.mc.player.posX, mc.player.posY - 1.0, NoSlowDown.mc.player.posZ), EnumFacing.DOWN));
	}
}