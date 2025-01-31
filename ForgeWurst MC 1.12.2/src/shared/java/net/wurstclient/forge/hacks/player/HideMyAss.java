/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;

public final class  HideMyAss extends Hack {

	public HideMyAss() {
		super("HideMyAss", "Turns all modules off.");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		for (Hack hack : ForgeWurst.getForgeWurst().getHax().getValues()) {
			if (hack.isEnabled()) {
				hack.setEnabled(false);
			}
		}
		onDisable();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
}