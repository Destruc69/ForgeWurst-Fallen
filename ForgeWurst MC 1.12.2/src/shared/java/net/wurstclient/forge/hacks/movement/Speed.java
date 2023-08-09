/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;

public final class Speed extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", "The speed of the speed (lol)", 0.2, 0.1, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting jump =
			new CheckboxSetting("Jump", "Jump on ground, Strafing.",
					false);

	private final CheckboxSetting ncpGlide =
			new CheckboxSetting("NCP-Glide", "I found speed and gliding worked on NCP as long \n." +
					"as your not falling",
					false);
	public Speed() {
		super("Speed", "I Show Speed");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
		addSetting(jump);
		addSetting(ncpGlide);
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
		if (jump.isChecked()) {
			if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
				if (mc.player.onGround) {
					mc.player.jump();
				}
			}
		}

		if (ncpGlide.isChecked() ) {
			if (mc.player.fallDistance > 0) {
				mc.player.motionY *= 0.8 + Math.random() * 0.1;
			}
		}

		MathUtils.setSpeed(speed.getValueF());
	}
}
