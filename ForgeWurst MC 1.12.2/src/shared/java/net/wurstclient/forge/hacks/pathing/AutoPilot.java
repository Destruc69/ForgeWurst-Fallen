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
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;

import java.util.ArrayList;

public final class AutoPilot extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	private PathfinderAStar pathfinderAStar;

	private boolean a;

	public static final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	public static final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.INTEGER);

	public static final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final CheckboxSetting setToCurrentPos =
			new CheckboxSetting("SetToCurrentPos", "Sets the X and Z and Y slider to the players current coordinate.",
					false);

	public AutoPilot() {
		super("AutoPilot", "Pathfinds the player towards a coordinate.");
		setCategory(Category.PATHING);
		addSetting(x);
		addSetting(y);
		addSetting(z);
		addSetting(setToCurrentPos);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		blockPosArrayList = new ArrayList<>();

		a = true;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		blockPosArrayList.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (setToCurrentPos.isChecked()) {
			x.setValue(mc.player.posX);
			y.setValue(mc.player.posY);
			z.setValue(mc.player.posZ);
			setToCurrentPos.setChecked(false);
			return;
		}

		if (PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.GROUND) {
			if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(0).getX(), mc.player.posY, blockPosArrayList.get(0).getZ()) >= mc.gameSettings.renderDistanceChunks * 14 || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), mc.player.lastTickPosY, blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) <= 1) {
				a = false;
				pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValueF(), z.getValue()), false);
				pathfinderAStar.compute();
				blockPosArrayList = pathfinderAStar.getPath();
			}

			if (PathfinderModule.isAuto()) {
				double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, mc.player.isSprinting() ? 0.2 : 0.26);
				mc.player.motionX = toMove[0];
				mc.player.motionZ = toMove[1];

				if (!PathfinderAStar.isEntityMoving(mc.player) && mc.player.onGround) {
					mc.player.jump();
				}
			}
		} else {
			if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(0).getX(), mc.player.posY, blockPosArrayList.get(0).getZ()) >= mc.gameSettings.renderDistanceChunks * 14 || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), mc.player.lastTickPosY, blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) <= 1) {
				a = false;
				pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValueF(), z.getValue()), true);
				pathfinderAStar.compute();
				blockPosArrayList = pathfinderAStar.getPath();
			}

			if (PathfinderModule.isAuto()) {
				if (mc.player.posY > PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
					mc.player.motionY = -mc.player.getDistance(mc.player.posX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.posZ);

					mc.player.motionX = 0;
					mc.player.motionZ = 0;
				} else if (mc.player.posY < PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
					mc.player.motionY = mc.player.getDistance(mc.player.posX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.posZ);

					mc.player.motionX = 0;
					mc.player.motionZ = 0;
				} else {
					mc.player.motionY = 0;
				}

				double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, PathfinderModule.airPathfinderBaseSpeed.getValueF());
				mc.player.motionX = toMove[0];
				mc.player.motionZ = toMove[1];
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!blockPosArrayList.isEmpty()) {
			PathfinderAStar.render(blockPosArrayList, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
		}
	}
}