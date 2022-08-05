/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.TimerUtils;

public final class Cheststealer extends Hack {
	double slot;
	public Cheststealer() {
		super("ChestStealer", "Steals items from chest.");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		TimerUtils.reset();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.currentScreen instanceof GuiChest) {
			if (TimerUtils.hasReached(80)) {
				if (slot + 1 < 27) {
					slot = slot + 1;
				}
				TimerUtils.reset();
			}
			GuiChest getState = (GuiChest) mc.currentScreen;
			if (!getState.inventorySlots.getSlot((int) slot).getStack().getItem().equals(Items.AIR)) {
				mc.playerController.windowClick(getState.inventorySlots.windowId, (int) slot, 0, ClickType.QUICK_MOVE, mc.player);
				double emptySlot = InventoryUtil.getEmptySlot();
				mc.playerController.windowClick(getState.inventorySlots.getSlot((int) emptySlot).slotNumber, (int) emptySlot,0, ClickType.QUICK_MOVE, mc.player);
				mc.playerController.updateController();
			}
		} else {
			slot = 0;
		}
	}
}