/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RotationUtils;

import java.util.ArrayList;

public final class BedWars extends Hack {
	ArrayList<BlockPos> theBeds = new ArrayList<>();

	public BedWars() {
		super("BedWarsBreaker", "Break beds automatically for bed wars");
		setCategory(Category.GAMES);
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
		breakBed();
		int radius = 4;
		for (int x = (int) -radius; x <= radius; x++) {
			for (int z = (int) -radius; z <= radius; z++) {
				for (int y = (int) -radius; y <= radius; y++) {
					BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
					if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.BED)) {
						theBeds.add(blockPos);
					}
				}
			}
		}
	}

	public void breakBed() {
		for (BlockPos blockPosss : theBeds) {
			BlockUtils.breakBlockSimple(blockPosss);
			lookAtPacket(blockPosss.getX(), blockPosss.getY(), blockPosss.getZ(), mc.player);
			lookAtPacket(blockPosss.getX(), blockPosss.getY(), blockPosss.getZ(), mc.player);
			lookAtPacket(blockPosss.getX(), blockPosss.getY(), blockPosss.getZ(), mc.player);
		}
	}

	public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
		double dirx = me.posX - px;
		double diry = me.posY - py;
		double dirz = me.posZ - pz;

		double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

		dirx /= len;
		diry /= len;
		dirz /= len;

		double pitch = Math.asin(diry);
		double yaw = Math.atan2(dirz, dirx);

		pitch = pitch * 180.0d / Math.PI;
		yaw = yaw * 180.0d / Math.PI;

		yaw += 90f;

		return new double[]{yaw, pitch};
	}

	private static void setYawAndPitch(float yaw1, float pitch1) {
		RotationUtils.faceVectorPacket(new Vec3d(yaw1, pitch1, 0));
	}

	private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
		double[] v = calculateLookAt(px, py, pz, me);
		setYawAndPitch((float) v[0], (float) v[1]);
	}
}