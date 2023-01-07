/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.world.Scaffold;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.PlayerControllerUtils;

public final class AutoItemFrameDupe extends Hack {
	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.ITEMFRAME);
	public static double shulkerSlot = 0;
	public AutoItemFrameDupe() {
		super("AutoItemFrameDupe", "Preforms the ItemFrame dupe.");
		setCategory(Category.PLAYER);
		addSetting(mode);
	}

	private enum Mode {
		ITEMFRAME("ItemFrame", true, false),
		CRAFT("Craft", false, true);

		private final String name;
		private final boolean itemframe;
		private final boolean craft;

		private Mode(String name, boolean itemframe, boolean craft) {
			this.name = name;
			this.itemframe = itemframe;
			this.craft = craft;
		}

		public String toString() {
			return name;
		}
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		shulkerSlot = 0;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		shulkerSlot = 0;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().itemframe) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityItemFrame) {
					if (((EntityItemFrame) entity).getDisplayedItem().isEmpty()) {
						mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
					} else {
						if (mc.player.ticksExisted % 20 == 0) {
							mc.playerController.attackEntity(mc.player, entity);
						} else {
							mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
						}
					}
					mc.player.swingArm(EnumHand.MAIN_HAND);
					mc.playerController.updateController();
				}
			}
			if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox)) {
				PlayerControllerUtils.windowClick_QUICK_MOVE((int) shulkerSlot);
				PlayerControllerUtils.windowClick_QUICK_MOVE((int) InventoryUtil.getHandSlot());
			}

		} else if (mode.getSelected().craft) {
			//Not yet done.
		}
		for (int x = 0; x < 27 + 4 + 9; x++) {
			if (mc.player.inventory.getStackInSlot(x).getItem() instanceof ItemShulkerBox) {
				shulkerSlot = x;
			}
		}
	}
}