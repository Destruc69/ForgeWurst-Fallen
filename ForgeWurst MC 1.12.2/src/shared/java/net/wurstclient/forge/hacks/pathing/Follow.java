/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.LandPathUtils;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;

public final class Follow extends Hack {

	private final CheckboxSetting onlyEntityPlayers =
			new CheckboxSetting("OnlyEntityPlayers", "Only follow entity players.",
					false);

	public Follow() {
		super("Follow", "Bot that follows entitys.");
		setCategory(Category.PATHING);
		addSetting(onlyEntityPlayers);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
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

			if (closestEntity != null) {
				if (onlyEntityPlayers.isChecked() && !(closestEntity instanceof EntityPlayer)) {
					return; // Skip processing if onlyEntityPlayers flag is set and the closest entity is not a player
				}

				double[] rots = LandPathUtils.getYawAndPitchForPath(mc.player.getPosition().add(0, -1, 0), blockPosArrayList, PathfinderModule.smoothingFactor.getValue());

				if (!LandPathUtils.isOnPath(blockPosArrayList) || blockPosArrayList.size() == 0 || LandPathUtils.isEntityMoving(closestEntity)) {
					if (mc.player.ticksExisted % 60 == 0) {
						blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition().add(0, -1, 0), closestEntity.getPosition().add(0, -1, 0), PathfinderModule.debug.isChecked());
						if (blockPosArrayList.size() > 0) {
							if (LandPathUtils.calculateETA(blockPosArrayList) != null) {
								ChatUtils.message("ETA: " + LandPathUtils.calculateETA(blockPosArrayList));
							}
						}
					}
				}

				if (PathfinderModule.isAuto()) {
					if (blockPosArrayList.size() > 0) {
						mc.player.rotationYaw = (float) rots[0];
						mc.player.rotationPitch = (float) rots[1];
						LandPathUtils.movementsEngage(PathfinderModule.safetyPlus.isChecked());
					} else {
						LandPathUtils.resetMovements();
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
			if (blockPosArrayList.size() > 0) {
				LandPathUtils.render(PathfinderModule.isRenderTesla(), (ArrayList<BlockPos>) blockPosArrayList, 1, PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
			}
		} catch (Exception ignored) {
		}
	}
}