/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.TimerUtils;

import java.util.Objects;

public final class EntityFlight extends Hack {

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", 1, 0.1, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", 1, 0.1, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting vel =
			new CheckboxSetting("Velocity",
					false);

	private final CheckboxSetting anti =
			new CheckboxSetting("AntiKick",
					false);

	private final CheckboxSetting stuck =
			new CheckboxSetting("AntiStuck",
					false);

	public EntityFlight() {
		super("EntityFlight", "Fly with Entitys/Ridables.\n" +
				"Go down pressing S or your keybind to walk back");
		setCategory(Category.MOVEMENT);
		addSetting(upSpeed);
		addSetting(downSpeed);
		addSetting(vel);
		addSetting(anti);
		addSetting(stuck);
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
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionY += upSpeed.getValueF();
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionY -= downSpeed.getValueF();
			}
			if (vel.isChecked()) {
				if (!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.player.getRidingEntity().setVelocity(mc.player.getRidingEntity().motionX, 0, mc.player.getRidingEntity().motionZ);
				}
			}
			if (anti.isChecked()) {
				if (!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
					if (mc.player.ticksExisted % 2 == 0) {
						Objects.requireNonNull(mc.player.getRidingEntity().motionY += 0.10123 / 2);
					} else {
						Objects.requireNonNull(mc.player.getRidingEntity().motionY -= 0.91873 / 2);
					}
				}
			}
			if (stuck.isChecked()) {
				for (Entity entity : mc.world.loadedEntityList) {
					if (entity instanceof EntityBoat || entity instanceof EntityHorse) {
						if (!mc.player.isRiding() && mc.player.getDistance(entity) < 3) {
							for (int x = 0; x < 6; x ++) {
								RotationUtils.faceVectorPacket(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));
							}
							mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
