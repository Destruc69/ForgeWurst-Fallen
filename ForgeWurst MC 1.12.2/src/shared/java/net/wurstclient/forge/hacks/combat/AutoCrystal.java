/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
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

import java.util.ArrayList;

public final class AutoCrystal extends Hack {

	private ArrayList<EntityEnderCrystal> entityEnderCrystals;

	public AutoCrystal() {
		super("AutoCrystal", "Auto Crystal but for Killaura.");
		setCategory(Category.COMBAT);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		entityEnderCrystals = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity != mc.player && mc.player.getDistance(entity) <= 4 && entity instanceof EntityPlayer) {
				BlockPos blockPos = getCrystalPos((EntityPlayer) entity);
				if (mc.player.ticksExisted % 10 == 0) {
					mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos.add(0, -1, 0), EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);

					float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ()).addVector(0.5, 0.5, 0.5));

					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos.add(0, -1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, (float) (blockPos.getX() + 0.5), (float) (blockPos.getY() + 0.5) , (float) (blockPos.getZ() + 0.5)));
				}
			}
			if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ) <= 4) {
				if (!entityEnderCrystals.contains(entity)) {
					entityEnderCrystals.add((EntityEnderCrystal) entity);
				}

				EntityEnderCrystal entityEnderCrystal = entityEnderCrystals.get(0);
				if (mc.player.ticksExisted % 20 == 0) {
					mc.playerController.attackEntity(mc.player, entityEnderCrystal);
					mc.player.swingArm(EnumHand.MAIN_HAND);

					float[] rot = RotationUtils.getNeededRotations(new Vec3d(entityEnderCrystal.lastTickPosX, entityEnderCrystal.lastTickPosY, entityEnderCrystal.lastTickPosZ).addVector(0.5, 0.5, 0.5));

					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
					mc.player.connection.sendPacket(new CPacketUseEntity(entityEnderCrystal, EnumHand.MAIN_HAND));
				}
			}
			entityEnderCrystals.removeIf(entity1 -> !entity1.isEntityAlive());
		}
	}

	private BlockPos getCrystalPos(EntityPlayer entityPlayer) {
		BlockPos blockPos = entityPlayer.getPosition();

		if (mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, -1, 0)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(1, 0, 0);
		} else if (mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(-1, -1, 0)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(-1, 0, 0);
		} else if (mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, -1, 1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(0, 0, 1);
		} else if (mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, -1, -1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(0, 0, -1);
		} else if (mc.world.getBlockState(blockPos.add(1, 0, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, -1, 1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(1, 0, 1);
		} else if (mc.world.getBlockState(blockPos.add(-1, 0, -1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(-1, -1, -1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(-1, 0, -1);
		} else if (mc.world.getBlockState(blockPos.add(1, 0, -1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, -1, -1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(1, 0, -1);
		} else if (mc.world.getBlockState(blockPos.add(-1, 0, 1)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(-1, -1, 1)).getBlock().equals(Blocks.AIR)) {
			return blockPos.add(-1, 0, 1);
		} else {
			return new BlockPos(0, 0, 0);
		}
	}
}