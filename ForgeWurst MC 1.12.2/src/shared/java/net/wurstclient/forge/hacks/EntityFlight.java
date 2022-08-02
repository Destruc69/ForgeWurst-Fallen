/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
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
			if (anti.isChecked()) {
				if (mc.player.fallDistance > 1) {
					if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown()) {
						if (TimerUtils.hasReached(50)) {
							Objects.requireNonNull(mc.player.getRidingEntity()).setPosition(mc.player.posX, mc.player.posY - 0.5, mc.player.posZ);
						} else if (TimerUtils.hasReached(100)) {
							TimerUtils.reset();
							Objects.requireNonNull(mc.player.getRidingEntity()).setPosition(mc.player.posX, mc.player.posY + 0.5, mc.player.posZ);
						}
					}
				}
			}

			if (TimerUtils.hasReached(50)) {

				TimerUtils.reset();

				try {
					if (stuck.isChecked()) {
						for (Entity entity : mc.world.loadedEntityList) {
							if (entity instanceof EntityBoat) {
								if (mc.player.getDistance(entity) < 4.5) {
									if (mc.player.getRidingEntity() == null) {
										mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
										lookAtPacket(entity.posX, entity.posY, entity.posZ, mc.player);
									}
								}
							}

							if (entity instanceof EntityHorse) {
								if (mc.player.getDistance(entity) < 4.5) {
									if (mc.player.getRidingEntity() == null) {
										mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
										lookAtPacket(entity.posX, entity.posY, entity.posZ, mc.player);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionY += upSpeed.getValueF();
			}

			if (mc.gameSettings.keyBindBack.isKeyDown()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionY -= downSpeed.getValueF();
			}

			if (vel.isChecked()) {
				if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
					Objects.requireNonNull(mc.player.getRidingEntity()).motionY = 0;
					mc.player.getRidingEntity().setVelocity(mc.player.getRidingEntity().motionX, 0, mc.player.getRidingEntity().motionZ);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
		double dirx = me.posX - px;
		double diry = me.posY - py;
		double dirz = me.posZ - pz;

		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

		dirx /= len;
		diry /= len;
		dirz /= len;

		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);

		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;

		yaw += 90f;

		return new double[]{yaw, pitch};
	}

	private static void setYawAndPitch(float yaw1, float pitch1) {
		RotationUtils.faceVectorPacket(new Vec3d(yaw1, pitch1, 0));
	}

	private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
		double[] v = calculateLookAt(px, py, pz, me);
		setYawAndPitch((float) v[0], (float) v[1]);
	}
}
