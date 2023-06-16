/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.TimerUtils;

public final class CPvPViewer extends Hack {
	public CPvPViewer() {
		super("CPvPViewer", "View other people fighting.");
		setCategory(Category.RENDER);
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
		for (Entity entity : mc.world.getLoadedEntityList()) {
			if (entity instanceof EntityPlayer) {
				if (entity.lastTickPosY < mc.player.posY) {
					mc.setRenderViewEntity(entity);
					mc.player.inventory.copyInventory(((EntityPlayer) entity).inventory);
					ItemStack itemStackMain = new ItemStack(((EntityPlayer) entity).getHeldItemMainhand().getItem());
					ItemStack itemStackOff = new ItemStack(((EntityPlayer) entity).getHeldItemOffhand().getItem());
					mc.player.getHeldItemMainhand().getItem().setContainerItem(itemStackMain.getItem());
					mc.player.getHeldItemOffhand().getItem().setContainerItem(itemStackOff.getItem());
					mc.ingameGUI.setOverlayMessage(entity.getName() + " | " + ((EntityPlayer) entity).getHealth(), false);
				}
			}
		}
	}
}
