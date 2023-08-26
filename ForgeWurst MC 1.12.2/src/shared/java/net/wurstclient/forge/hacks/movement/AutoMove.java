/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

import java.util.Objects;

public final class AutoMove extends Hack {

	private final CheckboxSetting forward =
			new CheckboxSetting("Forward",
					false);

	private final CheckboxSetting right =
			new CheckboxSetting("Right",
					false);

	private final CheckboxSetting back =
			new CheckboxSetting("Back",
					false);

	private final CheckboxSetting left =
			new CheckboxSetting("Left",
					false);

	private final CheckboxSetting lockX =
			new CheckboxSetting("LockX",
					false);

	private final CheckboxSetting lockZ =
			new CheckboxSetting("LockZ",
					false);

	public AutoMove() {
		super("AutoMove", "Makes you move automatically.");
		setCategory(Category.MOVEMENT);
		addSetting(forward);
		addSetting(right);
		addSetting(back);
		addSetting(left);
		addSetting(lockX);
		addSetting(lockZ);
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
		if (!mc.player.isRiding()) {
			if (lockX.isChecked()) {
				mc.player.motionX = 0;
			}
			if (lockZ.isChecked()) {
				mc.player.motionZ = 0;
			}
		} else {
			if (lockX.isChecked()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionX = 0;
			}
			if (lockZ.isChecked()) {
				Objects.requireNonNull(mc.player.getRidingEntity()).motionZ = 0;
			}
		}
	}

	@SubscribeEvent
	public void onInput(InputUpdateEvent event) {
		if (forward.isChecked()) {
			event.getMovementInput().moveForward++;
			event.getMovementInput().forwardKeyDown = true;
		}
		if (right.isChecked()) {
			event.getMovementInput().moveStrafe--;
			event.getMovementInput().rightKeyDown = true;
		}
		if (left.isChecked()) {
			event.getMovementInput().moveStrafe++;
			event.getMovementInput().leftKeyDown = true;
		}
		if (back.isChecked()) {
			event.getMovementInput().moveForward--;
			event.getMovementInput().backKeyDown = true;
		}
	}
}