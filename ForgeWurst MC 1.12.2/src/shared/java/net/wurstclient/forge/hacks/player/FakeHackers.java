/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class FakeHackers extends Hack {

	private final CheckboxSetting limb =
			new CheckboxSetting("LimbSwing", "Just makes there leg look wobbly and crazy",
					false);

	private final CheckboxSetting vel =
			new CheckboxSetting("Velocity", "Makes the players velocity to 0 and make them freeze yet still hit you",
					false);

	private final CheckboxSetting yaw =
			new CheckboxSetting("Yaw", "Changes the players yaw rapidly",
					false);

	public FakeHackers() {
		super("FakeHackers", "False report players with evidence!.");
		setCategory(Category.PLAYER);
		addSetting(limb);
		addSetting(vel);
		addSetting(yaw);
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
		for (Entity e : mc.world.loadedEntityList) {
			if (e != mc.player) {
				if (e instanceof EntityPlayer) {
					if (limb.isChecked()) {
						((EntityPlayer) e).limbSwing += 999f;
					}
					if (vel.isChecked()) {
						e.setVelocity(0, 0, 0);
					}

					if (yaw.isChecked()) {
						((EntityPlayer) e).rotationYawHead += 2f;
					}
				}
			}
		}
	}
}