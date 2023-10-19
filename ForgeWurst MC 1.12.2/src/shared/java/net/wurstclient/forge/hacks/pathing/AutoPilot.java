/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.PathfinderAStar;
import net.wurstclient.forge.pathfinding.PathfinderAStarAIR;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.ArrayList;

public final class AutoPilot extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	private PathfinderAStar pathfinderAStar;
	private PathfinderAStarAIR pathfinderAStarAIR;

	// This is for when just enabled it can get the pathfinding going
	private boolean a;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.AIR);

	private final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.INTEGER);

	private final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final CheckboxSetting setToCurrentPos =
			new CheckboxSetting("SetToCurrentPos", "Sets the X and Z and Y slider to the players current coordinate.",
					false);

	public static final SliderSetting baseSpeed = new SliderSetting("AirPathfinderBaseSpeed", 1, 0, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private enum Mode {
		GROUND("Ground"),
		AIR("Air");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public AutoPilot() {
		super("AutoPilot", "Pathfinds the player towards a coordinate.");
		setCategory(Category.PATHING);
		addSetting(mode);
		addSetting(x);
		addSetting(y);
		addSetting(z);
		addSetting(setToCurrentPos);
		addSetting(baseSpeed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		a = true;

		blockPosArrayList = new ArrayList<>();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		blockPosArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			if (setToCurrentPos.isChecked()) {
				x.setValue(mc.player.lastTickPosX);
				y.setValue(mc.player.lastTickPosY);
				z.setValue(mc.player.lastTickPosZ);
				setToCurrentPos.setChecked(false);
				return;
			}

			if (mode.getSelected() == Mode.GROUND) {
				if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), blockPosArrayList.get(blockPosArrayList.size() - 1).getY(), blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) < 1 ||
						mc.player.getDistance(blockPosArrayList.get(0).getX(), blockPosArrayList.get(0).getY(), blockPosArrayList.get(0).getZ()) > mc.gameSettings.renderDistanceChunks * 16) {
					a = false;
					pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValue(), z.getValue()));
					pathfinderAStar.compute();
					blockPosArrayList = pathfinderAStar.getPath();
					if (blockPosArrayList.size() > 0) {
						if (PathfinderAStar.calculateETA(blockPosArrayList) != null) {
							ChatUtils.message("ETA: " + PathfinderAStar.calculateETA(blockPosArrayList));
						}
					}
				}

				if (PathfinderModule.isAuto()) {
					if (blockPosArrayList.size() > 0) {

						double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, mc.player.isSprinting() ? 0.2 : 0.26);
						mc.player.motionX = toMove[0];
						mc.player.motionZ = toMove[1];

						if (mc.player.collidedHorizontally && mc.player.onGround) {
							mc.player.jump();
						}
					} else {
						//LandPathUtils.resetMovements();
					}
				}
			} else {
				if (!PathfinderAStarAIR.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), blockPosArrayList.get(blockPosArrayList.size() - 1).getY(), blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) < 1 ||
						mc.player.getDistance(blockPosArrayList.get(0).getX(), blockPosArrayList.get(0).getY(), blockPosArrayList.get(0).getZ()) > mc.gameSettings.renderDistanceChunks * 16) {
					a = false;
					pathfinderAStarAIR = new PathfinderAStarAIR(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValue(), z.getValue()));
					pathfinderAStarAIR.compute();
					blockPosArrayList = pathfinderAStarAIR.getPath();
					if (blockPosArrayList.size() > 0) {
						if (PathfinderAStar.calculateETA(blockPosArrayList) != null) {
							ChatUtils.message("ETA: " + PathfinderAStarAIR.calculateETA(blockPosArrayList));
						}
					}
				}

				if (PathfinderModule.isAuto()) {
					if (blockPosArrayList.size() > 0) {

						double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, baseSpeed.getValueF());
						mc.player.motionX = toMove[0];
						mc.player.motionZ = toMove[1];

						if (PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY() > mc.player.lastTickPosY) {
							mc.player.motionY = mc.player.getDistance(mc.player.lastTickPosX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.lastTickPosZ) / 2;
						} else if (PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY() < mc.player.lastTickPosY) {
							mc.player.motionY = -mc.player.getDistance(mc.player.lastTickPosX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.lastTickPosZ) / 2;
						} else {
							mc.player.motionY = 0;
						}
					} else {
						//LandPathUtils.resetMovements();
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!blockPosArrayList.isEmpty()) {
			PathfinderAStar.render(PathfinderModule.isRenderTesla(), blockPosArrayList, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
		}
	}
}