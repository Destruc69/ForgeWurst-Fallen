/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class AntiHunger extends Hack {

	private final CheckboxSetting cancelSprintPacket =
			new CheckboxSetting("CancelSprintPacket",
					false);

	private final CheckboxSetting groundSpoof =
			new CheckboxSetting("GroundSpoof",
					false);

	public AntiHunger() {
		super("AntiHunger", "Reduces hunger rate.");
		setCategory(Category.MOVEMENT);
		addSetting(cancelSprintPacket);
		addSetting(groundSpoof);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		if (cancelSprintPacket.isChecked()) {
			try {
				if (mc.player.isSprinting()) {
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
				}
			} catch (Exception ignored) {
			}
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (cancelSprintPacket.isChecked()) {
			if (event.getPacket() instanceof CPacketEntityAction) {
				CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
				if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING)) {
					event.setCanceled(true);
				}
			}
		}

		if (groundSpoof.isChecked()) {
			if (mc.player.onGround && !mc.playerController.getIsHittingBlock()) {
				if (mc.player.motionY > -0.01 && mc.player.motionY < 0.01) {
					if (event.getPacket() instanceof CPacketPlayer) {
						event.setPacket(new CPacketPlayer(false));
					}
					if (event.getPacket() instanceof CPacketPlayer.Rotation) {
						CPacketPlayer.Rotation cPacketPlayerRotation = (CPacketPlayer.Rotation) event.getPacket();
						event.setPacket(new CPacketPlayer.Rotation(cPacketPlayerRotation.getYaw(0), cPacketPlayerRotation.getPitch(0), false));
					}
					if (event.getPacket() instanceof CPacketPlayer.Position) {
						CPacketPlayer.Position cPacketPlayerPosition = (CPacketPlayer.Position) event.getPacket();
						event.setPacket(new CPacketPlayer.Position(cPacketPlayerPosition.getX(0), cPacketPlayerPosition.getY(1), cPacketPlayerPosition.getZ(0), false));
					}
					if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
						CPacketPlayer.PositionRotation cPacketPlayerPositionRotation = (CPacketPlayer.PositionRotation) event.getPacket();
						event.setPacket(new CPacketPlayer.PositionRotation(cPacketPlayerPositionRotation.getX(0), cPacketPlayerPositionRotation.getY(0), cPacketPlayerPositionRotation.getZ(0), cPacketPlayerPositionRotation.getYaw(0), cPacketPlayerPositionRotation.getPitch(0), false));
					}
				}
			}
		}
	}
}