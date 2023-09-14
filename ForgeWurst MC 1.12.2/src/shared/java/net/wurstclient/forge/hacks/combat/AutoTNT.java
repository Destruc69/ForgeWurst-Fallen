/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.RotationUtils;

public final class AutoTNT extends Hack {

	public AutoTNT() {
		super("AutoTNT", "Places TNT near entity's and ignites the TNT.");
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
		EntityLivingBase closestEntity = findClosestEntity();

		if (closestEntity != null) {
			BlockPos tntPos = findTNTPlacementPosition(closestEntity);

			if (tntPos != null) {
				placeTNTAndIgnite(tntPos);
			}
		}
	}

	private BlockPos findTNTPlacementPosition(EntityLivingBase entity) {
		BlockPos enBP = entity.getPosition().add(0.5, 0, 0.5);
		BlockPos blockPos = null;
		if (mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(0, 0, 1);
		} else if (mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(0, 0, -1);
		} else if (mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(1, 0, 0);
		} else if (mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.TNT) ) {
			blockPos = enBP.add(-1, 0, 0);
		} else if (mc.world.getBlockState(enBP.add(1, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(-1, 1, 0)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(1, 1, 0);
		} else if (mc.world.getBlockState(enBP.add(-1, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(1, 1, 0)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(-1, 1, 0);
		} else if (mc.world.getBlockState(enBP.add(0, 1, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 1, 1)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(0, 1, 1);
		} else if (mc.world.getBlockState(enBP.add(0, 1, -1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(enBP.add(0, 0, -1)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(enBP.add(0, 1, -1)).getBlock().equals(Blocks.TNT)) {
			blockPos = enBP.add(0, 1, -1);
		}
		return blockPos;
	}

	private EntityLivingBase findClosestEntity() {
		EntityLivingBase closestEntity = null;
		double closestDistance = Double.MAX_VALUE;

		for (Entity entity : mc.player.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityLivingBase && entity != mc.player) {
				double distance = mc.player.getDistanceSq(entity.posX, entity.posY, entity.posZ);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEntity = (EntityLivingBase) entity;
				}
			}
		}
		assert closestEntity != null;
		if (mc.player.getDistance(closestEntity) < 5) {
			return closestEntity;
		} else {
			return null;
		}
	}

	private void placeTNTAndIgnite(BlockPos tntPos) {
		if (mc.world.getBlockState(tntPos).getBlock() == Blocks.TNT) {
			mc.player.inventory.currentItem = getSlot(Items.FLINT_AND_STEEL);
			mc.playerController.updateController();
			if (mc.player.ticksExisted % 5 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, tntPos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);

				float[] rot = RotationUtils.getNeededRotations(new Vec3d(tntPos.getX() + 0.5, tntPos.getY() + 0.5, tntPos.getZ() + 0.5));
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
			}
		} else {
			mc.player.inventory.currentItem = getSlot(Item.getItemFromBlock(Blocks.TNT));
			mc.playerController.updateController();
			if (mc.player.ticksExisted % 5 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, tntPos, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);

				float[] rot = RotationUtils.getNeededRotations(new Vec3d(tntPos.getX() + 0.5, tntPos.getY(), tntPos.getZ() + 0.5));
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
			}
		}
	}

	private int getSlot(Item item) {
		for (int i = 0; i < mc.player.inventory.mainInventory.size(); i ++) {
			if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
				return i;
			}
		}
		return 0;
	}
}