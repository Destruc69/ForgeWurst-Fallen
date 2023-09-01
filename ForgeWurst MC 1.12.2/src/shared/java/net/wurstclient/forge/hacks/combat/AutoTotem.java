/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class AutoTotem extends Hack {

	private int nextTickSlot = -1;
	private boolean wasTotemInOffhand = false;
	private int timer = 0;

	private final SliderSetting delay = new SliderSetting("Delay",
			"Amount of ticks to wait before equipping the next totem.", 0, 0, 20, 1,
			SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting health = new SliderSetting("Health",
			"Effectively disables AutoTotem until your health reaches this value or falls below it.\n"
					+ "0 = always active", 0, 0, 20, 1,
			SliderSetting.ValueDisplay.INTEGER);

	public AutoTotem() {
		super("AutoTotem", "Always use totem in off-hand.");
		setCategory(Category.COMBAT);
		addSetting(delay);
		addSetting(health);
	}

	//
	// WURST-7 AUTOTOTEM
	//

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
		finishMovingTotem();

		EntityPlayerSP player = mc.player;
		InventoryPlayer inventory = player.inventory;
		int nextTotemSlot = searchForTotems(inventory);

		ItemStack offhandStack = inventory.getStackInSlot(40);
		if (isTotem(offhandStack.getItem())) {
			wasTotemInOffhand = true;
			return;
		}

		if (wasTotemInOffhand) {
			timer = delay.getValueI();
			wasTotemInOffhand = false;
		}

		float healthF = (float) health.getValue();
		if (healthF > 0 && player.getHealth() > healthF * 2F)
			return;

		if (mc.currentScreen instanceof GuiContainer
				&& !(mc.currentScreen instanceof GuiInventory))
			return;

		if (nextTotemSlot == -1)
			return;

		if (timer > 0) {
			timer--;
			return;
		}

		moveTotem(nextTotemSlot, offhandStack);
	}

	private void moveTotem(int nextTotemSlot, ItemStack offhandStack) {
		boolean offhandEmpty = offhandStack.isEmpty();

		EntityPlayerSP player = mc.player;
		mc.playerController.windowClick(player.inventoryContainer.windowId, nextTotemSlot, 0, ClickType.PICKUP, player);
		mc.playerController.windowClick(player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, player);

		if (!offhandEmpty)
			nextTickSlot = nextTotemSlot;
	}

	private void finishMovingTotem() {
		if (nextTickSlot == -1)
			return;

		EntityPlayerSP player = mc.player;
		mc.playerController.windowClick(player.inventoryContainer.windowId, nextTickSlot, 0, ClickType.PICKUP, player);
		nextTickSlot = -1;
	}

	private int searchForTotems(InventoryPlayer inventory) {
		int nextTotemSlot = -1;

		for (int slot = 0; slot <= 36; slot++) {
			if (!isTotem(inventory.getStackInSlot(slot).getItem()))
				continue;

			if (nextTotemSlot == -1)
				nextTotemSlot = slot < 9 ? slot + 36 : slot;
		}

		return nextTotemSlot;
	}

	private boolean isTotem(Item item) {
		return item == Items.TOTEM_OF_UNDYING;
	}
}