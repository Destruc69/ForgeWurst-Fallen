/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.PacketUtils;

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
		try {
			if (cancelSprintPacket.isChecked()) {
				if (event.getPacket() instanceof CPacketEntityAction) {
					CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
					if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING)) {
						event.setCanceled(true);
					}
				}
			}

			if (groundSpoof.isChecked()) {
				if (event.getPacket() instanceof CPacketPlayer) {
					if (mc.player.onGround) {
						if (mc.playerController.getIsHittingBlock())
							return;
						CPacketPlayer oldPacket = (CPacketPlayer) event.getPacket();
						double x = oldPacket.getX(-1);
						double y = oldPacket.getY(-1);
						double z = oldPacket.getZ(-1);
						float yaw = oldPacket.getYaw(-1);
						float pitch = oldPacket.getPitch(-1);

						Packet<?> newPacket;
						if (PacketUtils.changesPosition(oldPacket))
							if (PacketUtils.changesLook(oldPacket))
								newPacket =
										new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, false);
							else
								newPacket =
										new CPacketPlayer.Position(x, y, z, false);
						else if (PacketUtils.changesLook(oldPacket))
							newPacket =
									new CPacketPlayer.Rotation(yaw, pitch, false);
						else
							newPacket = new CPacketPlayer(false);

						event.setPacket(newPacket);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}
}