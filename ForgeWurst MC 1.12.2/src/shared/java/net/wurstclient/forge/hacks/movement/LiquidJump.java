/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WGetLiquidCollisionBoxEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class LiquidJump extends Hack {

	private boolean canjump;
	private int delay;
	private int stage;
	private int timer;

	public LiquidJump() {
		super("LiquidJump", "Allows you to jump on liquids. \n" +
				"This is a generic bypass.");
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
		if (mc.player.collidedVertically && !canjump && mc.world.getBlockState(mc.player.getPosition().add(0, -1, 0)).getBlock() instanceof BlockLiquid) {
			++delay;
			stage = 0;
			mc.player.motionY = 0.1;
		} else if (mc.player.onGround && !(mc.world.getBlockState(getBlockAtPosC(mc.player, 0.0, 1.0, 0.0)).getBlock() instanceof BlockLiquid)) {
			canjump = false;
			delay = 0;
		}

		if (delay > 2) {
			canjump = true;
		}

		if (canjump) {
			++stage;
			double moty = mc.player.motionY;
			switch (stage) {
				case 1: {
					moty = 0.5;
					break;
				}
				case 2: {
					moty = 0.483;
					break;
				}
				case 3: {
					moty = 0.46;
					break;
				}
				case 4: {
					moty = 0.42;
					break;
				}
				case 5: {
					moty = 0.388;
					break;
				}
				case 6: {
					moty = 0.348;
					break;
				}
				case 7: {
					moty = 0.316;
					break;
				}
				case 8: {
					moty = 0.284;
					break;
				}
				case 9: {
					moty = 0.252;
					break;
				}
				case 10: {
					moty = 0.22;
					break;
				}
				case 11: {
					moty = 0.188;
					break;
				}
				case 12: {
					moty = 0.166;
					break;
				}
				case 13: {
					moty = 0.165;
					break;
				}
				case 14: {
					moty = 0.16;
					break;
				}
				case 15: {
					moty = 0.136;
					break;
				}
				case 16: {
					moty = 0.11;
					break;
				}
				case 17: {
					moty = 0.111;
					break;
				}
				case 18: {
					moty = 0.1095;
					break;
				}
				case 19: {
					moty = 0.073;
					break;
				}
				case 20: {
					moty = 0.085;
					break;
				}
				case 21: {
					moty = 0.06;
					break;
				}
				case 22: {
					moty = 0.036;
					break;
				}
				case 23: {
					moty = 0.04;
					break;
				}
				case 24: {
					moty = 0.03;
					break;
				}
				case 25: {
					moty = 0.004;
					break;
				}
				case 26: {
					moty = 0.0025;
					break;
				}
				case 27: {
					moty = 0.002;
					break;
				}
				case 28: {
					moty = 0.0015;
					break;
				}
				case 29: {
					moty = -0.025;
					break;
				}
				case 30: {
					moty = -0.06;
					break;
				}
				case 31: {
					moty = -0.09;
					break;
				}
				case 32: {
					moty = -0.12;
					break;
				}
				case 33: {
					moty = -0.15;
					break;
				}
				case 34: {
					moty = -0.18;
					break;
				}
				case 35: {
					moty = -0.21;
					break;
				}
				case 36: {
					moty = -0.24;
					break;
				}
				case 37: {
					moty = -0.28;
					break;
				}
				case 38: {
					moty = -0.34;
					break;
				}
				case 39: {
					moty = -0.4;
					break;
				}
				case 40: {
					moty = -0.46;
					break;
				}
				case 41: {
					moty = -0.52;
					break;
				}
				case 42: {
					moty = -0.58;
					break;
				}
				case 43: {
					moty = -0.65;
					break;
				}
				case 44: {
					moty = -0.71;
					break;
				}
				case 45: {
					this.canjump = false;
				}
			}
			mc.player.motionY = moty;
		}

		if (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F && !mc.player.isSneaking() && getColliding(0)) {
			int delay = 40;
			if (timer < delay) {
				++timer;
			} else {
				++timer;
				if (timer < delay + 5) {
					mc.player.motionX = 0.1;
				} else if (timer < delay + 20 && timer > delay + 10) {
					mc.player.motionZ = -0.1;
				} else if (timer < delay + 30 && timer > delay + 20) {
					mc.player.motionX = -0.1;
				} else if (timer < delay + 40 && timer > delay + 30) {
					mc.player.motionZ = 0.1;
				}
				if (timer > delay + 40) {
					timer = delay;
				}
			}
		} else {
			timer = 0;
		}
	}

	@SubscribeEvent
	public void onGetLiquidCollisionBox(WGetLiquidCollisionBoxEvent event) {
		event.setSolidCollisionBox();
	}


	public static BlockPos getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
		return new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z);
	}

	private boolean getColliding(int i) {
		int mx = i;
		int mz = i;
		int max = i;
		int maz = i;
		if (mc.player.motionX > 0.01) {
			mx = 0;
		} else if (mc.player.motionX < -0.01) {
			max = 0;
		}
		if (mc.player.motionZ > 0.01) {
			mz = 0;
		} else if (mc.player.motionZ < -0.01) {
			maz = 0;
		}
		int xmin = MathHelper.floor(mc.player.getEntityBoundingBox().minX - (double) mx);
		int ymin = MathHelper.floor(mc.player.getEntityBoundingBox().minY - 1.0);
		int zmin = MathHelper.floor(mc.player.getEntityBoundingBox().minZ - (double) mz);
		int xmax = MathHelper.floor(mc.player.getEntityBoundingBox().minX + (double) max);
		int ymax = MathHelper.floor(mc.player.getEntityBoundingBox().minY + 1.0);
		int zmax = MathHelper.floor(mc.player.getEntityBoundingBox().minZ + (double) maz);
		for (int x = xmin; x <= xmax; x++) {
			for (int y = ymin; y <= ymax; y++) {
				for (int z = zmin; z <= zmax; z++) {
					Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block instanceof BlockLiquid && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.player.isInsideOfMaterial(Material.WATER)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}