/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;

public final class AutoDupe extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.DONKEY);

	public AutoDupe() {
		super("AutoDupe", "Automates 1.12.2 dupes.");
		setCategory(Category.WORLD);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		try {
			ChatUtils.message("AutoDupe is not completed yet.");
			setEnabled(false);
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().donkey) {
			donkeyDupe();
		} else if (mode.getSelected().crafting) {
			craftingDupe();
		}
	}

	private void craftingDupe() {

	}

	private void donkeyDupe() {
		if (mc.player.getRidingEntity() instanceof EntityDonkey && mc.currentScreen instanceof GuiScreenHorseInventory) {

		} else {

		}
	}

	private enum Mode {
		CRAFTING("Crafting", true, false),
		DONKEY("Donkey", false, true);

		private final String name;
		private final boolean crafting;
		private final boolean donkey;

		private Mode(String name, boolean crafting, boolean donkey) {
			this.name = name;
			this.donkey = donkey;
			this.crafting = crafting;
		}

		public String toString() {
			return name;
		}
	}
}