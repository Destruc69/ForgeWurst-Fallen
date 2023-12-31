/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.PathfinderAStar;
import net.wurstclient.forge.settings.BlockListSetting;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

import java.util.ArrayList;

public final class AutoPilot extends Hack {

	private ArrayList<BlockPos> blockPosArrayList;

	private PathfinderAStar pathfinderAStar;

	private boolean a;
	private boolean b;

	public static final SliderSetting x =
			new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	public static final SliderSetting y =
			new SliderSetting("Y", 0, 0, 256, 1, SliderSetting.ValueDisplay.INTEGER);

	public static final SliderSetting z =
			new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

	private final CheckboxSetting setToCurrentPos =
			new CheckboxSetting("SetToCurrentPos", "Sets the X and Z and Y slider to the players current coordinate.",
					false);



	private final BlockListSetting blockListSetting = new BlockListSetting("Blocks", "First index in the list if focused. If air than will dismissed the coordinates, Else will autopilot towars a block that is in the first index.", Blocks.AIR);

	public AutoPilot() {
		super("AutoPilot", "Pathfinds the player towards a coordinate.");
		setCategory(Category.PATHING);
		addSetting(x);
		addSetting(y);
		addSetting(z);
		addSetting(setToCurrentPos);
		addSetting(blockListSetting);
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


		//String save = "";
		//String nextInstruction = PathfinderAStar.getNextInstruction(mc.player.getPosition(), blockPosArrayList);

		//if (blockPosArrayList.size() > 0) {
		//	if (!nextInstruction.equals(save)) {
		//		// The instruction has changed, so you can trigger your event here
		//		save = nextInstruction;

		//		TTSWrapper.say(save);
		//	}
		//}

		if (blockListSetting.getBlockNames().isEmpty()) {
			if (PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.GROUND) {
				if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(0).getX(), mc.player.posY, blockPosArrayList.get(0).getZ()) >= mc.gameSettings.renderDistanceChunks * 14 || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), mc.player.lastTickPosY, blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) <= 1) {
					a = false;
					pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValueF(), z.getValue()), PathfinderAStar.TYPE.GROUND);
					pathfinderAStar.compute();
					blockPosArrayList = pathfinderAStar.getPath();
				}

				if (PathfinderModule.isAuto()) {
					double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, mc.player.isSprinting() ? 0.2 : 0.26);
					mc.player.motionX = toMove[0];
					mc.player.motionZ = toMove[1];

					jump(PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY() > mc.player.lastTickPosY && mc.player.onGround || (mc.player.isInWater() && !mc.player.collidedHorizontally) );
				}
			} else if (PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.AIR || PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.ELYTRA) {
				if (PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.AIR) {
					if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(0).getX(), mc.player.posY, blockPosArrayList.get(0).getZ()) >= mc.gameSettings.renderDistanceChunks * 14 || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), mc.player.lastTickPosY, blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) <= 1) {
						a = false;
						pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValueF(), z.getValue()), PathfinderAStar.TYPE.AIR);
						pathfinderAStar.compute();
						blockPosArrayList = pathfinderAStar.getPath();
					}
				} else {
					if (!PathfinderAStar.isOnPath(blockPosArrayList) || a || mc.player.getDistance(blockPosArrayList.get(0).getX(), mc.player.posY, blockPosArrayList.get(0).getZ()) >= mc.gameSettings.renderDistanceChunks * 14 || mc.player.getDistance(blockPosArrayList.get(blockPosArrayList.size() - 1).getX(), mc.player.lastTickPosY, blockPosArrayList.get(blockPosArrayList.size() - 1).getZ()) <= 1) {
						a = false;
						pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), new BlockPos(x.getValue(), y.getValueF(), z.getValue()), PathfinderAStar.TYPE.ELYTRA);
						pathfinderAStar.compute();
						blockPosArrayList = pathfinderAStar.getPath();
					}
				}

				if (PathfinderModule.isAuto()) {
					//if (mc.player.posY > PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
					//	mc.player.motionY = -mc.player.getDistance(mc.player.posX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.posZ);
					//	mc.player.motionX = 0;
					//	mc.player.motionZ = 0;
					//} else if (mc.player.posY < PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
					//	mc.player.motionY = mc.player.getDistance(mc.player.posX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.posZ);
					//	mc.player.motionX = 0;
					//	mc.player.motionZ = 0;
					//} else {
					//	mc.player.motionY = 0;
					//}
					double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, PathfinderModule.airPathfinderBaseSpeed.getValue());
					mc.player.motionX = toMove[0];
					mc.player.motionZ = toMove[1];

					if (mc.player.lastTickPosY < PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
						mc.player.motionY = mc.player.getDistance(mc.player.lastTickPosX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.lastTickPosZ);
					} else if (mc.player.lastTickPosY > PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY()) {
						mc.player.motionY = -mc.player.getDistance(mc.player.lastTickPosX, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY(), mc.player.lastTickPosZ);
					} else {
						mc.player.motionY = 0;
					}
				}
			}
		} else {
			Block block = Block.getBlockFromName(blockListSetting.getBlockNames().get(0));
			BlockPos blockPos = PathfinderAStar.findNearestReachableBlock(block);

			assert blockPos != null;

			if (PathfinderModule.actionTypeEnumSetting.getSelected().equals(PathfinderModule.ActionType.GROUND)) {
				if (mc.world.getBlockState(blockPos).getBlock().equals(block)) {
					if (blockPosArrayList.isEmpty() || !PathfinderAStar.isOnPath(blockPosArrayList) || a) {
						pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), blockPos, PathfinderAStar.TYPE.GROUND);
						pathfinderAStar.compute();
						blockPosArrayList = pathfinderAStar.getPath();
						a = false;
					}
				}
			} else if (PathfinderModule.actionTypeEnumSetting.getSelected() == PathfinderModule.ActionType.AIR) {
				if (mc.world.getBlockState(blockPos).getBlock().equals(block)) {
					if (blockPosArrayList.isEmpty() || !PathfinderAStar.isOnPath(blockPosArrayList) || a) {
						pathfinderAStar = new PathfinderAStar(mc.player.getPosition(), blockPos, PathfinderAStar.TYPE.AIR);
						pathfinderAStar.compute();
						blockPosArrayList = pathfinderAStar.getPath();
						a = false;
					}
				}
			}

			if (PathfinderModule.isAuto()) {
				if (PathfinderModule.actionTypeEnumSetting.getSelected().equals(PathfinderModule.ActionType.GROUND)) {
					if (blockPosArrayList.size() > 0) {
						double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, mc.player.isSprinting() ? 0.2 : 0.26);
						mc.player.motionX = toMove[0];
						mc.player.motionZ = toMove[1];

						KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, PathfinderAStar.getTargetPositionInPathArray(blockPosArrayList).getY() > mc.player.lastTickPosY && mc.player.onGround || mc.player.isInWater());
					}
				} else {
					if (blockPosArrayList.size() > 0) {
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
						double[] toMove = PathfinderAStar.calculateMotion(blockPosArrayList, mc.player.rotationYaw, PathfinderModule.airPathfinderBaseSpeed.getValue());
						mc.player.motionX = toMove[0];
						mc.player.motionZ = toMove[1];
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!blockPosArrayList.isEmpty()) {
			PathfinderAStar.render(blockPosArrayList, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueI(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
		}
	}

	private void jump(boolean u) {
		if (u) {
			if (mc.player.onGround) {
				mc.player.jump();
			} else if (mc.player.isInWater()) {
				mc.player.motionY = 0.005;
			}
		}
	}
}