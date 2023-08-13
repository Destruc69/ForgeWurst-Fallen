/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.LandPathUtils;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;

public final class AutoPilot extends Hack {
	public AutoPilot() {
		super("AutoPilot", "Pathfinds the player towards a coordinate.");
		setCategory(Category.PATHING);
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
	private static Vec3d vec3d;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		BlockPos targetPos = new BlockPos(vec3d.x, vec3d.y, vec3d.z);
		if (!LandPathUtils.isOnPath(blockPosArrayList) || blockPosArrayList.isEmpty()) {
			if (mc.player.ticksExisted % 60 == 0) {
				blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition().add(0, -1, 0), targetPos, PathfinderModule.debug.isChecked());
				if (!blockPosArrayList.isEmpty()) {
					String eta = LandPathUtils.calculateETA(blockPosArrayList);
					if (eta != null) {
						ChatUtils.message(TextFormatting.GREEN + "ETA: " + eta);
					}
				}
			}
		}
		if (PathfinderModule.isAuto()) {
			if (!blockPosArrayList.isEmpty()) {
				double[] rots = LandPathUtils.getYawAndPitchForPath(mc.player.getPosition().add(0, -1, 0), blockPosArrayList, PathfinderModule.smoothingFactor.getValue());
				mc.player.rotationYaw = (float) rots[0];
				mc.player.rotationPitch = (float) rots[1];
				LandPathUtils.movementsEngage(PathfinderModule.safetyPlus.isChecked());
			} else {
				LandPathUtils.resetMovements();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
			if (!blockPosArrayList.isEmpty()) {
				LandPathUtils.render(PathfinderModule.isRenderTesla(), blockPosArrayList, 1, PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
			}
		} catch (Exception ignored) {
		}
	}

	public static void setTarg(double x, double y, double z) {
		vec3d = new Vec3d(x, y, z);
	}
}