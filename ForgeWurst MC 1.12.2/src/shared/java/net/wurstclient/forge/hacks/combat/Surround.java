/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.init.Blocks;
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

public final class Surround extends Hack {

	public Surround() {
		super("Surround", "Places blocks around you, ideal for crystal pvp.");
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
		Vec3d vec3d = new Vec3d(mc.player.posX + 0.5, mc.player.posY, mc.player.posZ + 0.5);
		BlockPos bp1 = new BlockPos(vec3d.x + 1, vec3d.y, vec3d.z);
		BlockPos bp2 = new BlockPos(vec3d.x - 1, vec3d.y, vec3d.z);
		BlockPos bp3 = new BlockPos(vec3d.x, vec3d.y, vec3d.z + 1);
		BlockPos bp4 = new BlockPos(vec3d.x, vec3d.y, vec3d.z - 1);

		if (mc.world.getBlockState(bp1).getBlock().equals(Blocks.AIR)) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, bp1, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(bp1.getX(), bp1.getY(), bp1.getZ()));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		} else if (mc.world.getBlockState(bp2).getBlock().equals(Blocks.AIR)) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, bp2, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(bp2.getX(), bp2.getY(), bp2.getZ()));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		} else if (mc.world.getBlockState(bp3).getBlock().equals(Blocks.AIR)) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, bp3, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(bp3.getX(), bp3.getY(), bp3.getZ()));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		} else if (mc.world.getBlockState(bp4).getBlock().equals(Blocks.AIR)) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, bp4, EnumFacing.DOWN, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(bp4.getX(), bp4.getY(), bp4.getZ()));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		} else {
			setEnabled(false);
		}
	}
}