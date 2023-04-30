/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.block.Block;
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
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class Surround extends Hack {

	public static double startYaw = 0;
	public static double startPitch = 0;

	public static Vec3d vec3d = null;

	private final CheckboxSetting toggle =
			new CheckboxSetting("Toggle", "Should we toggle the module?.",
					false);

	public Surround() {
		super("Surround", "Places blocks around you, ideal for crystal pvp.");
		setCategory(Category.COMBAT);
		addSetting(toggle);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		vec3d = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
		startYaw = mc.player.rotationYaw;
		startPitch = mc.player.rotationPitch;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		vec3d = null;
		mc.player.rotationYaw = (float) startYaw;
		mc.player.rotationPitch = (float) startPitch;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		mc.player.moveToBlockPosAndAngles(new BlockPos(vec3d.x, vec3d.y, vec3d.z), mc.player.rotationYaw, mc.player.rotationPitch);
		BlockPos blockPos1 = new BlockPos(vec3d.addVector(1, 0, 0)).add(0.5, 0.5, 0.5);
		BlockPos blockPos2 = new BlockPos(vec3d.addVector(-1, 0, 0)).add(0.5, 0.5, 0.5);
		BlockPos blockPos3 = new BlockPos(vec3d.addVector(0, 0, 1)).add(0.5, 0.5, 0.5);
		BlockPos blockPos4 = new BlockPos(vec3d.addVector(0, 0, -1)).add(0.5, 0.5, 0.5);
		if (mc.player.ticksExisted % 5 == 0) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos1, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos1.getX(), blockPos1.getY(), blockPos1.getZ()).addVector(0.5, 0.5, 0.5));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		}
		if (mc.player.ticksExisted % 10 == 0) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos2, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ()).addVector(0.5, 0.5, 0.5));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		}
		if (mc.player.ticksExisted % 15 == 0) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos3, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos3.getX(), blockPos3.getY(), blockPos3.getZ()).addVector(0.5, 0.5, 0.5));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		}
		if (mc.player.ticksExisted % 20 == 0) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos4, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos4.getX(), blockPos4.getY(), blockPos4.getZ()).addVector(0.5, 0.5, 0.5));
			mc.player.rotationYaw = rot[0];
			mc.player.rotationPitch = rot[1];
		}
		mc.player.swingArm(EnumHand.MAIN_HAND);
		if (!toggle.isChecked()) {
			if (mc.world.getBlockState(blockPos1).getBlock().equals(Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem())) && mc.world.getBlockState(blockPos2).getBlock().equals(Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem())) && mc.world.getBlockState(blockPos3).getBlock().equals(Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem())) && mc.world.getBlockState(blockPos4).getBlock().equals(Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem()))) {
				setEnabled(false);
			}
		}
	}
}