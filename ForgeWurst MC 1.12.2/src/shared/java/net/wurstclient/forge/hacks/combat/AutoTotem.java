/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class AutoTotem extends Hack {
	public static double slotToPick;

	public AutoTotem() {
		super("AutoTotem", "Always use totem in off-hand.");
		setCategory(Category.COMBAT);
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
		if (mc.player.getHeldItemOffhand().getItem().equals(Items.AIR)) {
			if (InventoryUtil.hasHotbarItem(Items.TOTEM_OF_UNDYING)) {
				for (int x = 0; x < mc.player.inventory.mainInventory.size(); x++) {
					if (mc.player.inventory.mainInventory.get(x).getItem().equals(Items.TOTEM_OF_UNDYING)) {
						slotToPick = x;
					}
				}
				int slot = (int) slotToPick;
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
						ClickType.PICKUP, mc.player);
				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP,
						mc.player);

				mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0,
						ClickType.PICKUP, mc.player);
				mc.playerController.updateController();
			}
		}
	}
}