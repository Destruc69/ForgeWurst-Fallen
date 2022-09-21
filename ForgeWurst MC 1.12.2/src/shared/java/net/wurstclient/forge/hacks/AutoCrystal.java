/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.RotationUtils;

import java.util.ArrayList;

public final class AutoCrystal extends Hack {

	public static ArrayList<EntityEnderCrystal> enderCrystals = new ArrayList<>();
	public static Entity targetCrystal;
	public static BlockPos targetPos;

	public AutoCrystal() {
		super("AutoCrystal", "Auto Crystal but for Killaura.");
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
	public void getCrystals(WUpdateEvent event) {
		try {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityEnderCrystal) {
					if (!enderCrystals.contains(entity)) {
						enderCrystals.add((EntityEnderCrystal) entity);
					}
				}
				if (entity instanceof EntityPlayer && !(entity == mc.player)) {
					if (mc.player.getDistance(entity) < 4) {
						for (int x = -2; x < 2; x++) {
							for (int z = -2; z < 2; z++) {
								if (mc.world.getBlockState(entity.getPosition().add(x, 0, z)).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock().equals(Blocks.BEDROCK))) {
									targetPos = entity.getPosition().add(x, -1, z);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SubscribeEvent
	public void place(WUpdateEvent event) {
		try {
			if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
				Items.END_CRYSTAL.onItemUse(mc.player, mc.world, targetPos, EnumHand.MAIN_HAND, EnumFacing.DOWN, (float) mc.objectMouseOver.hitVec.x, (float) mc.objectMouseOver.hitVec.y, (float) mc.objectMouseOver.hitVec.z);
				RotationUtils.faceVectorPacket(new Vec3d(targetPos.getX(), targetPos.getY(), targetPos.getZ()).addVector(0.5, 0.5, 0.5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void breakk(WUpdateEvent event) {
		try {
			RotationUtils.faceVectorPacket(new Vec3d(targetCrystal.lastTickPosX, targetCrystal.lastTickPosY, targetCrystal.lastTickPosZ).addVector(0.5, 0.5, 0.5));
			mc.playerController.attackEntity(mc.player, targetCrystal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}