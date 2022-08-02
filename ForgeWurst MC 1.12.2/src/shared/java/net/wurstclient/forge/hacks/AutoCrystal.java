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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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

public final class AutoCrystal extends Hack {

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
	public void update(WUpdateEvent event) {
		if (mc.player.ticksExisted % 4 == 0) {
			if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
				place();
			}
		} else {
			attack();
		}
	}

	public void place() {
		for (Entity entity : mc.world.loadedEntityList) {
			assert entity != mc.player;
			if (entity != mc.player) {
				if (mc.player.getDistance(entity.posX, entity.posY, entity.posZ) < 3) {
					if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL))
						return;

					if (entity != mc.player) {
						for (int x = -1; x < 1; x++) {
							for (int z = -1; z < 1; z++) {
								if (mc.world.getBlockState(entity.getPosition().add(x, 0, z)).getBlock()
										.equals(Blocks.AIR)
										&& (mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock()
										.equals(Blocks.OBSIDIAN)
										|| mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock()
										.equals(Blocks.BEDROCK))) {
									assert entity != mc.player;
									placeCrystal(entity.getPosition().add(x, -1, z));
								}
							}
						}
					}
				}
			}
		}
	}

	public void attack() {
		for (Entity entity : mc.world.loadedEntityList) {
			assert entity != mc.player;
			if (entity != mc.player) {
				if (entity instanceof EntityEnderCrystal) {
					if (mc.player.getDistance(entity.posX, entity.posY, entity.posZ) < 3) {
						attackCrystal((EntityEnderCrystal) entity);
					}
				}
			}
		}
	}

	public static void placeCrystal(BlockPos pos) {
		RotationUtils.faceVectorPacket(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
		mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, mc.objectMouseOver.hitVec, EnumHand.MAIN_HAND);
		mc.player.swingArm(EnumHand.MAIN_HAND);
	}

	public static void attackCrystal(EntityEnderCrystal entityEnderCrystal) {
		RotationUtils.faceVectorPacket(new Vec3d(entityEnderCrystal.posX, entityEnderCrystal.posY, entityEnderCrystal.posZ));
		mc.playerController.attackEntity(mc.player, entityEnderCrystal);
	}
}