/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class NoSlowDown extends Hack {

	private final CheckboxSetting ncp =
			new CheckboxSetting("NCP", "Bypass NCP ABORT_DESTROY_BLOCK",
					false);

	private final CheckboxSetting tbtb =
			new CheckboxSetting("2b2t", "Bypass 2b2t START_SNEAKING",
					false);

	private final CheckboxSetting hypixel =
			new CheckboxSetting("Hypixel", "Bypass hypixel/ncp modifications TryUseItemOnBlock.",
					false);

	private final CheckboxSetting hypixel2 =
			new CheckboxSetting("Hypixel2", "Bypass hypixel/ncp modifications 2nd method START_DESTROY_BLOCK.",
					false);

	private final CheckboxSetting other =
			new CheckboxSetting("Other", "Bypass other anticheats/servers RELEASE_USE_ITEM.",
					false);

	private final CheckboxSetting other2 =
			new CheckboxSetting("Other2", "Bypass other anticheats/servers 2nd method SWAP_HELD_ITEMS.",
					false);

	private final CheckboxSetting other3 =
			new CheckboxSetting("Other3", "Bypass other anticheats/servers 3nd method CPacketHeldItemChange.",
					false);

	public NoSlowDown() {
		super("NoSlowDown", "No time to slow down when eating");
		setCategory(Category.MOVEMENT);
		addSetting(ncp);
		addSetting(tbtb);
		addSetting(hypixel);
		addSetting(hypixel2);
		addSetting(other);
		addSetting(other2);
		addSetting(other3);
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
		try {
			if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood) {
				event.getMovementInput().moveForward *= 5;
				event.getMovementInput().moveStrafe *= 5;
				if (ncp.isChecked()) {
					mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ), EnumFacing.DOWN));
				}
				if (other.isChecked()) {
					mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ), EnumFacing.DOWN));
				}
				if (tbtb.isChecked()) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
				}
				if (other2.isChecked()) {
					mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ), EnumFacing.DOWN));
				}
				if (other3.isChecked()) {
					mc.player.connection.sendPacket(new CPacketHeldItemChange(getHandSlot()));
				}
				if (hypixel.isChecked()) {
					mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ), EnumFacing.DOWN, EnumHand.MAIN_HAND, (float) mc.player.getLookVec().x, (float) mc.player.getLookVec().y, (float) mc.player.getLookVec().z));
				}
				if (hypixel2.isChecked()) {
					mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ), EnumFacing.DOWN));
				}
			} else {
				if (tbtb.isChecked()) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
				}
			}
		} catch (Exception ignored) {
		}
	}

	private int getHandSlot() {
		return mc.player.inventory.currentItem;
	}
}