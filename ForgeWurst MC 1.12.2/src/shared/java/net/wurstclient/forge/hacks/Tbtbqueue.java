/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.TextUtil;

public final class Tbtbqueue extends Hack {
	public Tbtbqueue() {
		super("2b2tQueue", "Plays a sound when your near to joining \n" +
				"And will AntiAFK until your back\n" +
				TextUtil.coloredString("WILL LEAVE THE SERVER IF YOU TAKE DAMAGE", TextUtil.Color.RED));
		setCategory(Category.MOVEMENT);
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
		try {
			//Really basic check if there in the void which means there in the queue world
			BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 3, mc.player.posZ);
			if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
				if (mc.ingameGUI.getChatGUI().getSentMessages().contains("5") ||
						mc.ingameGUI.getChatGUI().getSentMessages().contains("4") ||
						mc.ingameGUI.getChatGUI().getSentMessages().contains("3") ||
						mc.ingameGUI.getChatGUI().getSentMessages().contains("2") ||
						mc.ingameGUI.getChatGUI().getSentMessages().contains("1")) {
					if (mc.player.ticksExisted % 3 == 0) {
						mc.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, 1);
					}
				}
			} else {
				if (mc.player.ticksExisted % 3 == 0) {
					mc.player.rotationYaw = mc.player.rotationYaw + 5;
					mc.player.rotationPitch = mc.player.rotationPitch + 1;
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
				} else {
					mc.player.rotationPitch = mc.player.rotationPitch - 1;
					mc.player.rotationYaw = mc.player.rotationYaw - 2;
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
				}
				if (mc.player.hurtTime > 0) {
					mc.player.connection.onDisconnect(new TextComponentString("You took damage, You didnt come back so we had no choice but too leave"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}