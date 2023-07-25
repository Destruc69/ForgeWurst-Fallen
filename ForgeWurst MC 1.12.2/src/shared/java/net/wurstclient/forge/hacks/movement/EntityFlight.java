/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

import java.util.Objects;

public final class EntityFlight extends Hack {

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Bypass some anti cheats.",
					false);

	private final CheckboxSetting velocity =
			new CheckboxSetting("Velocity", "Holds you still in the air if jump is not present.",
					false);

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", 1, 0.1, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", 1, 0.1, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	public EntityFlight() {
		super("EntityFlight", "Fly with Entitys/Ridables.\n" +
				"Go down pressing S or your keybind to walk back");
		setCategory(Category.MOVEMENT);
		addSetting(upSpeed);
		addSetting(downSpeed);
		addSetting(bypass);
		addSetting(velocity);
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
						assert mc.player.getRidingEntity() != null;
						if (mc.gameSettings.keyBindJump.isKeyDown()) {
							Objects.requireNonNull(mc.player.getRidingEntity()).motionY += upSpeed.getValueF();
						} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
							Objects.requireNonNull(mc.player.getRidingEntity()).motionY -= downSpeed.getValueF();
						}
					} else {
						if (mc.player.ticksExisted % 5 == 0) {
							assert mc.player.getRidingEntity() != null;
							if (mc.gameSettings.keyBindJump.isKeyDown()) {
								Objects.requireNonNull(mc.player.getRidingEntity()).motionY += upSpeed.getValueF();
							} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
								Objects.requireNonNull(mc.player.getRidingEntity()).motionY -= downSpeed.getValueF();
							}
						} else {
							if (mc.gameSettings.keyBindJump.isKeyDown()) {
								mc.player.getRidingEntity().motionY /= 2;
							}
						}
					}
					if (velocity.isChecked()) {
						if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
							mc.player.getRidingEntity().motionY = 0.04;
						}
					}

					//lil anti kick
					if (!(mc.player.getRidingEntity().onGround)) {
						if (mc.player.ticksExisted % 2 == 0) {
							mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX + 0.00000001, mc.player.getRidingEntity().posY + 0.00000001, mc.player.getRidingEntity().posZ - 0.00000001);
						} else {
							mc.player.getRidingEntity().setPosition(mc.player.getRidingEntity().posX - 0.00000001, mc.player.getRidingEntity().posY - 0.00000001, mc.player.getRidingEntity().posZ + 0.00000001);
						}
					}
				}
				//I find you can fly better when collided
				mc.player.getRidingEntity().collidedHorizontally = true;
				mc.player.getRidingEntity().collidedVertically = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onExitVehicle(EntityMountEvent event) {
		if (event.isDismounting()) {
			if (!(event.getEntityBeingMounted().onGround)) {
				event.setCanceled(true);
			}
		}
	}
}