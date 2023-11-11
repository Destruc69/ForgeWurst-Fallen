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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class CPvPViewer extends Hack {

	private EntityPlayer entityPlayer;

	private boolean a;

	public CPvPViewer() {
		super("CPvPViewer", "View other people fighting.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		a = false;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.setRenderViewEntity(mc.player);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (Entity entity : mc.world.getLoadedEntityList()) {
				if (entity instanceof EntityPlayer) {
					if (entity.lastTickPosY < mc.player.posY) {
						if (entity != mc.player) {
							if (mc.player.ticksExisted % 200 == 0 || !a) {
								a = true;
								this.entityPlayer = (EntityPlayer) entity;
								mc.setRenderViewEntity(entity);
							}
						}
					}
				}
			}

			if (entityPlayer != null) {

				mc.player.inventory.copyInventory(entityPlayer.inventory);

				for (int i = 0; i < 1000; i++) {
					mc.player.inventory.setInventorySlotContents(i, entityPlayer.inventory.getStackInSlot(i));
				}

				mc.player.inventory.currentItem = entityPlayer.inventory.currentItem;

				if (entityPlayer.isHandActive()) {
					mc.player.swingArm(entityPlayer.getActiveHand());
				}
			}
		} catch (Exception ignored) {
		}
	}
}
