/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.PlayerUtils;

public final class NoSlowDown extends Hack {

	public static double[] dir;

	private final CheckboxSetting ncp =
			new CheckboxSetting("NCP", "Bypass NCP", true);

	private final CheckboxSetting ncp2 =
			new CheckboxSetting("NCP2", "Bypass other/maybe custom NCP", false);


	private final CheckboxSetting tobetotee =
			new CheckboxSetting("2b2t", "Bypass 2B2T", true);

	public NoSlowDown() {
		super("NoSlowDown", "No time to slow down when eating");
		setCategory(Category.MOVEMENT);
		addSetting(ncp);
		addSetting(ncp2);
		addSetting(tobetotee);
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
		moveLogic();
		if (ncp.isChecked()) {
			ncpPacket();
		}
		if (tobetotee.isChecked()) {
			tbttPacket();
		}
		if (ncp2.isChecked()) {
			ncp2Packet();
		}
	}

	public void moveLogic() {
		if (!mc.gameSettings.keyBindSprint.isKeyDown()) {
			dir = MathUtils.directionSpeed(0.2);
		} else if (mc.gameSettings.keyBindSprint.isKeyDown()) {
			dir = MathUtils.directionSpeed(0.24);
		}
		if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem().equals(Items.BOW)) {
			{
				if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
					mc.player.motionX = dir[0];
					mc.player.motionZ = dir[1];
				}
			}
		}
	}

	public void ncpPacket() {
		if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem().equals(Items.BOW)) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, PlayerUtils.GetLocalPlayerPosFloored(), EnumFacing.DOWN));
		}
	}

	public void ncp2Packet() {
		if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem().equals(Items.BOW)) {
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, PlayerUtils.GetLocalPlayerPosFloored(), EnumFacing.DOWN));
		}
	}

	public void tbttPacket() {
		if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemMainhand().getItem().equals(Items.BOW)) {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
		} else {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
		}
	}
}
