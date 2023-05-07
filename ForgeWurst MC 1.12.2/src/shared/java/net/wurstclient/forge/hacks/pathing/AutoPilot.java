/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.LandPathUtils;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public final class AutoPilot extends Hack {

	public static ArrayList<BlockPos> blockPosArrayList;

	private static double xTarg;
	private static double yTarg;
	private static double zTarg;

	private static double xTargA;
	private static double yTargA;
	private static double zTargA;

	private final CheckboxSetting debug =
			new CheckboxSetting("Debug", "Debug mode.",
					false);

	private final EnumSetting<Mode> renderMode =
			new EnumSetting<>("RenderMode", Mode.values(), Mode.BARITONE);

	public static SliderSetting pathRed = new SliderSetting("Path red",
			"Path red", 0, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting pathGreen = new SliderSetting("Path green",
			"Path green", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting pathBlue = new SliderSetting("Path blue",
			"Path blue", 0, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting lineWidth = new SliderSetting("LineWidth",
			"The width of the lines for the renders", 1, 0.1, 10, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final EnumSetting<ModeType> modeType =
			new EnumSetting<>("ModeType", ModeType.values(), ModeType.AUTO);

	public static SliderSetting smoothingFactor = new SliderSetting("SmoothingFactor",
			"How smooth is the turning? \n" +
					"If its not smooth enough it may start spinning in circles.", 0.2, 0, 2, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	public AutoPilot() {
		super("AutoPilot", "Simple automation for navigation.");
		setCategory(Category.PATHING);
		addSetting(debug);
		addSetting(renderMode);
		addSetting(pathRed);
		addSetting(pathGreen);
		addSetting(pathBlue);
		addSetting(lineWidth);
		addSetting(modeType);
		addSetting(smoothingFactor);
	}

	private enum Mode {
		BARITONE("Baritone", true, false),
		TESLA("Tesla", false, true);

		private final String name;
		private final boolean baritone;
		private final boolean tesla;

		Mode(String name, boolean baritone, boolean tesla) {
			this.name = name;
			this.tesla = tesla;
			this.baritone = baritone;
		}

		public String toString() {
			return name;
		}
	}

	private enum Type {
		LAND("Land", true, false),
		AIR("Air", false, true);

		private final String name;
		private final boolean land;
		private final boolean air;

		private Type(String name, boolean land, boolean air) {
			this.name = name;
			this.air = air;
			this.land = land;
		}

		public String toString() {
			return name;
		}
	}

	private enum ModeType {
		AUTO("Auto", true),
		RENDER("Render", false);

		private final String name;
		private final boolean auto;

		ModeType(String name, boolean auto) {
			this.name = name;
			this.auto = auto;
		}

		public String toString() {
			return name;
		}
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg), debug.isChecked());
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, false);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) throws IOException {
		if (modeType.getSelected().auto) {
			mc.player.rotationYaw = (float) getYawAndPitchForPath(mc.player.getPosition(), blockPosArrayList)[0];

			//Basic stuff for terrain

			//Jumping when collided
			if (mc.player.onGround && mc.player.collidedHorizontally) {
				mc.player.jump();
			}

			//Swimming
			if (mc.player.isInWater() && !mc.player.collidedHorizontally) {
				mc.player.motionY = 0.05;
			}

			//Sprint jumping
			if (isYawStable(Math.round(mc.player.rotationYaw))) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, true);
			} else {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, false);
			}

			boolean onPath = false;
			int range = 2; // Check blocks 2 blocks away from the player in all directions

			for (int x = -range; x <= range; x++) {
				for (int y = -range; y <= range; y++) {
					for (int z = -range; z <= range; z++) {
						BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
						if (blockPosArrayList.contains(blockPos)) {
							onPath = true;
							break; // Break out of the loops once a block on the path is found
						}
					}
					if (onPath) {
						break; // Break out of the loops once a block on the path is found
					}
				}
				if (onPath) {
					break; // Break out of the loops once a block on the path is found
				}
			}

			if (!onPath) {
				blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg), debug.isChecked());
			}

			for (int y = -50; y < 50; y++) {
				if (mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTarg, yTarg, zTarg))) {
					setEnabled(false);
					ChatUtils.message("[AUTOPILOT] We have arrived, Disengaging.");
				} else if (mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTargA, yTargA, zTargA)) && !(mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTarg, yTarg, zTarg)))) {
					blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg), debug.isChecked());
					ChatUtils.message("Starting next section...");
				}
			}
		} else {
			if (mc.player.ticksExisted % 20 == 0) {
				blockPosArrayList = LandPathUtils.createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg), debug.isChecked());
			}
		}
	}

	private double prevYaw = 0;

	public boolean isYawStable(double yaw) {
		boolean isStable = Math.round(Math.abs(yaw - prevYaw)) < 0.01; // set a threshold value for stable yaw
		prevYaw = Math.round(yaw);
		return isStable;
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (renderMode.getSelected().baritone) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;
			ArrayList<BlockPos> path = blockPosArrayList; // Replace getPath() with the method that returns the ArrayList of BlockPos

			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(lineWidth.getValueF());
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(pathRed.getValueF(), pathGreen.getValueF(), pathBlue.getValueF(), 1.0f);

			for (int i = 0; i < path.size() - 1; i++) {
				BlockPos start = path.get(i);
				BlockPos end = path.get(i + 1);
				double startX = start.getX() + 0.5 - player.posX;
				double startY = start.getY() + 1.5 - player.posY;
				double startZ = start.getZ() + 0.5 - player.posZ;
				double endX = end.getX() + 0.5 - player.posX;
				double endY = end.getY() + 1.5 - player.posY;
				double endZ = end.getZ() + 0.5 - player.posZ;
				GL11.glVertex3d(startX, startY, startZ);
				GL11.glVertex3d(endX, endY, endZ);
			}
			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		} else if (renderMode.getSelected().tesla) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;
			ArrayList<BlockPos> path = blockPosArrayList; // Replace getPath() with the method that returns the ArrayList of BlockPos

			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(lineWidth.getValueF());

			// Calculate the player's direction vector
			Vec3d lookVec = player.getLook(1.0f);
			double lookX = lookVec.x;
			double lookZ = lookVec.z;

			// Calculate the offset vector perpendicular to the player's direction vector
			double offsetX = -lookZ;
			double offsetY = 0.0;
			double offsetZ = lookX;

			// Normalize the offset vector
			double offsetLength = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
			offsetX /= offsetLength;
			offsetZ /= offsetLength;

			// Draw the two lines
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4f(pathRed.getValueF(), pathGreen.getValueF(), pathBlue.getValueF(), 1.0f);

			for (int i = 0; i < path.size() - 1; i++) {
				BlockPos start = path.get(i);
				BlockPos end = path.get(i + 1);
				double startX = start.getX() + 0.5 - player.posX;
				double startY = start.getY() + 1.5 - player.posY;
				double startZ = start.getZ() + 0.5 - player.posZ;
				double endX = end.getX() + 0.5 - player.posX;
				double endY = end.getY() + 1.5 - player.posY;
				double endZ = end.getZ() + 0.5 - player.posZ;

				// Calculate the start and end points for the left line
				double leftStartX = startX + offsetX;
				double leftStartY = startY + offsetY;
				double leftStartZ = startZ + offsetZ;
				double leftEndX = endX + offsetX;
				double leftEndY = endY + offsetY;
				double leftEndZ = endZ + offsetZ;

				// Calculate the start and end points for the right line
				double rightStartX = startX - offsetX;
				double rightStartY = startY - offsetY;
				double rightStartZ = startZ - offsetZ;
				double rightEndX = endX - offsetX;
				double rightEndY = endY - offsetY;
				double rightEndZ = endZ - offsetZ;

				// Draw the left line
				GL11.glVertex3d(leftStartX, leftStartY, leftStartZ);
				GL11.glVertex3d(leftEndX, leftEndY, leftEndZ);

				// Draw the right line
				GL11.glVertex3d(rightStartX, rightStartY, rightStartZ);
				GL11.glVertex3d(rightEndX, rightEndY, rightEndZ);
			}

			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}


	}

	public static void setTarg(double x, double y, double z) {
		xTarg = x;
		yTarg = y;
		zTarg = z;
	}

	private double smoothYaw = 0;
	private double smoothPitch = 0;

	public double[] getYawAndPitchForPath(BlockPos playerPos, ArrayList<BlockPos> path) {
		double[] yawAndPitch = new double[]{0, 0};

		assert playerPos != null;
		assert path != null;

		if (!path.isEmpty()) {
			// Find closest block and calculate yaw and pitch
			int closestBlockIndex = 0;
			double closestBlockDistance = Double.POSITIVE_INFINITY;
			for (int i = 0; i < path.size(); i++) {
				double distance = playerPos.distanceSq(path.get(i));
				if (distance < closestBlockDistance) {
					closestBlockDistance = distance;
					closestBlockIndex = i;
				}
			}

			BlockPos closestBlock = path.get(closestBlockIndex);
			BlockPos nextBlock;
			if (closestBlockIndex == path.size() - 1) {
				nextBlock = closestBlock;
			} else {
				nextBlock = path.get(closestBlockIndex + 1);
			}

			double xDiff = nextBlock.getX() + 0.5 - playerPos.getX();
			double zDiff = nextBlock.getZ() + 0.5 - playerPos.getZ();
			double yDiff = nextBlock.getY() + 0.5 - (playerPos.getY() + 1.0);
			double distanceXZ = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(zDiff, 2));
			double targetYaw = Math.toDegrees(Math.atan2(zDiff, xDiff)) - 90;
			double targetPitch = Math.toDegrees(Math.atan2(-yDiff, distanceXZ));

			double diffYaw = MathHelper.wrapDegrees(targetYaw - smoothYaw);
			double diffPitch = MathHelper.wrapDegrees(targetPitch - smoothPitch);

			// Smooth the values using exponential moving average
			double SMOOTHING_FACTOR = smoothingFactor.getValue();
			smoothYaw += SMOOTHING_FACTOR * diffYaw;
			smoothPitch += SMOOTHING_FACTOR * diffPitch;

			yawAndPitch[0] = smoothYaw;
			yawAndPitch[1] = smoothPitch;

			return yawAndPitch;
		} else {
			ChatUtils.error("Error, path is empty, contact dev!");
		}
		return yawAndPitch;
	}
}