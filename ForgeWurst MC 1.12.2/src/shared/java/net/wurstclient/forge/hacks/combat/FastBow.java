/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class FastBow extends Hack {

	public FastBow() {
		super("FastBow", "Shoot bows faster.");
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
		if (mc.player.getHealth() > 0.0f && (mc.player.onGround || mc.player.capabilities.isCreativeMode) && mc.player.inventory.getCurrentItem() != null && mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown()) {
			mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
			for (int i = 0; i < 20; ++i) {
				mc.player.connection.sendPacket(new CPacketPlayer());
			}
			mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
			mc.player.inventory.getCurrentItem().getItem().onPlayerStoppedUsing(mc.player.inventory.getCurrentItem(), mc.world, mc.player, 10);
		}
	}
}