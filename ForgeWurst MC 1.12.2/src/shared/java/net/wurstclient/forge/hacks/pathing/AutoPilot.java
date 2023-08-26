/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.movement.ElytraFlight;
import net.wurstclient.forge.pathfinding.LandPathUtils;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;

public final class AutoPilot extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private enum Mode {
		NORMAL("Normal"),
		BETA("Beta");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private boolean hasCheckedIfGoToNether;
	private boolean shouldGoToNether;

	public AutoPilot() {
		super("AutoPilot", "Pathfinds the player towards a coordinate.");
		setCategory(Category.PATHING);
		addSetting(mode);
		addSetting(x);
		addSetting(y);
		addSetting(z);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);


		shouldGoToNether = false;
		hasCheckedIfGoToNether = false;

		blockPosArrayList = new ArrayList<>();

		if (mode.getSelected() == Mode.BETA) {
			setEnabled(false);
			try {
				ChatUtils.message("Beta mode is not yet ready...");
			} catch (Exception ignored) {
			}
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		blockPosArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected() == Mode.NORMAL) {
			if (!LandPathUtils.isOnPath(blockPosArrayList) || blockPosArrayList.size() < 2) {
				if (mc.player.ticksExisted % 60 == 0) {
					blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition().add(0, -1, 0), new BlockPos(x.getValue(), y.getValue(), z.getValue()), PathfinderModule.debug.isChecked());
					if (blockPosArrayList.size() > 0) {
						if (LandPathUtils.calculateETA(blockPosArrayList) != null) {
							ChatUtils.message("ETA: " + LandPathUtils.calculateETA(blockPosArrayList));
						}
					}
				}
			}

			if (PathfinderModule.isAuto()) {
				if (blockPosArrayList.size() > 0) {

					double[] toMove = LandPathUtils.calculateMotion(blockPosArrayList, mc.player.rotationYaw, LandPathUtils.isYawStable(mc.player.rotationYaw));
					mc.player.motionX = toMove[0];
					mc.player.motionZ = toMove[1];

					if (mc.player.onGround && mc.player.collidedHorizontally || mc.player.isInWater() && !mc.player.collidedHorizontally) {
						mc.player.jump();
					}
				} else {
					LandPathUtils.resetMovements();
				}
			}
		} else if (mode.getSelected() == Mode.BETA) {
			engageBetaMode();
		}
	}

	private void engageBetaMode() {
		// Define distance threshold
		final int OVERWORLD_DISTANCE_THRESHOLD = 5000;

		if (!hasCheckedIfGoToNether) {
			ChatUtils.message("Checking if we should go to the Nether...");

			double distanceToTarget = mc.player.getDistance(x.getValue(), y.getValue(), z.getValue());
			if (distanceToTarget > OVERWORLD_DISTANCE_THRESHOLD) {
				if (mc.player.dimension == -1) { // Player is in the Nether
					ChatUtils.message("Already in the Nether! Continuing...");
					shouldGoToNether = false;
				} else {
					ChatUtils.message("We need to find a Nether portal to enter the Nether...");
					shouldGoToNether = true;
				}
			} else {
				ChatUtils.message("Within distance to travel in the overworld...");
				shouldGoToNether = false;
			}

			hasCheckedIfGoToNether = true;
		} else {
			if (shouldGoToNether) {

			} else {

			}
		}
	}

	private void pathFindToBlock(Block block) {
		ArrayList<BlockPos> path = LandPathUtils.createPath(mc.player.getPosition().add(0, -1, 0), LandPathUtils.findNearestReachableBlock(block), PathfinderModule.debug.isChecked());

	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!blockPosArrayList.isEmpty()) {
			LandPathUtils.render(PathfinderModule.isRenderTesla(), blockPosArrayList, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
		}
	}
}