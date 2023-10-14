/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.PathfinderAStar;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.util.ArrayList;

public final class AutoPilot extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	private PathfinderAStar pathfinderAStar;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final CheckboxSetting setToCurrentPos =
			new CheckboxSetting("SetToCurrentPos", "Sets the X and Z and Y slider to the players current coordinate.",
					false);

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
		addSetting(setToCurrentPos);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);


		shouldGoToNether = false;
		hasCheckedIfGoToNether = false;

		blockPosArrayList = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		blockPosArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (setToCurrentPos.isChecked()) {
			x.setValue(mc.player.lastTickPosX);
			y.setValue(mc.player.lastTickPosY);
			z.setValue(mc.player.lastTickPosZ);
			setToCurrentPos.setChecked(false);
		}
		if (mode.getSelected() == Mode.NORMAL) {
			if (!PathfinderAStar.isOnPath(blockPosArrayList) || blockPosArrayList.size() <= 0 || blockPosArrayList.isEmpty()) {
				if (mc.player.ticksExisted % 60 == 0) {
					pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValue(), z.getValue()));
					pathfinderAStar.compute();
					if (blockPosArrayList.size() > 0) {
						if (PathfinderAStar.calculateETA(blockPosArrayList) != null) {
							ChatUtils.message("ETA: " + PathfinderAStar.calculateETA(blockPosArrayList));
						}
					}
				}
			}

			if (PathfinderModule.isAuto()) {
				if (blockPosArrayList.size() > 0) {

					double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, PathfinderAStar.isYawStable(mc.player.rotationYaw));
					mc.player.motionX = toMove[0];
					mc.player.motionZ = toMove[1];

					KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, mc.player.onGround && mc.player.collidedHorizontally || mc.player.isInWater() && !mc.player.collidedHorizontally);
				} else {
					//LandPathUtils.resetMovements();
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
				if (mc.player.dimension == 0) {
					if (PathfinderAStar.findNearestReachableBlock(Blocks.PORTAL) != null) {
						if (mc.player.ticksExisted % 20 == 0) {
							pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), PathfinderAStar.findNearestReachableBlock(Blocks.PORTAL));
							pathfinderAStar.compute();
							blockPosArrayList = pathfinderAStar.getPath();
						}
						if (pathfinderAStar.getPath().size() > 0) {
							double[] toMove = PathfinderAStar.calculateMotion(pathfinderAStar.getPath(), mc.player.rotationYaw, PathfinderAStar.isYawStable(mc.player.rotationYaw));
							mc.player.motionX = toMove[0];
							mc.player.motionZ = toMove[1];
						}
					} else {

					}
				} else if (mc.player.dimension == -1) {

				}
			} else {
				if (mc.player.ticksExisted % 20 == 0) {
					pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValue(), z.getValue()));
					pathfinderAStar.compute();
				}
				double[] toMove = PathfinderAStar.calculateMotion(pathfinderAStar.getPath(), mc.player.rotationYaw, PathfinderAStar.isYawStable(mc.player.rotationYaw));
				mc.player.motionX = toMove[0];
				mc.player.motionZ = toMove[1];
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!blockPosArrayList.isEmpty()) {
			PathfinderAStar.render(PathfinderModule.isRenderTesla(), blockPosArrayList, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
		}
	}
}