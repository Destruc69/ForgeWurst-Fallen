/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public final class EntitySpeed extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", 2, 0.5, 5, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Bypass some anti cheats.",
					false);

	public EntitySpeed() {
		super("EntitySpeed", "Move faster with Entitys/Ridables.");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(bypass);
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
			if (mc.player.getRidingEntity() != null) {
				if (Objects.requireNonNull(mc.player.getRidingEntity()).isEntityAlive()) {
					if (!bypass.isChecked()) {
				 		double[] dir = MathUtils.directionSpeed(speed.getValueF());
						assert mc.player.getRidingEntity() != null;
						Objects.requireNonNull(mc.player.getRidingEntity()).motionX = dir[0];
						mc.player.getRidingEntity().motionZ = dir[1];
					} else {
						if (mc.player.ticksExisted % 5 == 0) {
							double[] dir = MathUtils.directionSpeed(speed.getValueF() - Math.random() * 0.02);
							assert mc.player.getRidingEntity() != null;
							Objects.requireNonNull(mc.player.getRidingEntity()).motionX = dir[0];
							mc.player.getRidingEntity().motionZ = dir[1];
						} else {
							mc.player.getRidingEntity().motionX /= 2;
							mc.player.getRidingEntity().motionZ /= 2;
						}
					}
					mc.player.getRidingEntity().rotationYaw = mc.player.rotationYaw;

					((EntityTameable) Objects.requireNonNull(mc.player.getRidingEntity())).setTamed(true);
					((EntityTameable) Objects.requireNonNull(mc.player.getRidingEntity())).setTamedBy(mc.player);
					((EntityTameable) Objects.requireNonNull(mc.player.getRidingEntity())).setOwnerId(mc.player.getPersistentID());
					((EntityTameable) Objects.requireNonNull(mc.player.getRidingEntity())).setSitting(true);

					mc.player.getRidingEntity().setGlowing(true);

					((EntityHorse) mc.player.getRidingEntity()).setHorseSaddled(true);
					((EntityDonkey) mc.player.getRidingEntity()).setHorseSaddled(true);
					((EntityPig) mc.player.getRidingEntity()).setSaddled(true);

					if (mc.gameSettings.keyBindJump.isKeyDown() || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
						if (mc.player.ticksExisted % 20 == 0) {
							mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_RIDING_JUMP, 100));
						}
					}

					Objects.requireNonNull(mc.player.getRidingEntity()).rotationYaw = mc.player.rotationYaw;
					Objects.requireNonNull(mc.player.getRidingEntity()).rotationPitch = mc.player.rotationPitch;
				}
			}
		} catch (Exception e) {
		}
	}
}