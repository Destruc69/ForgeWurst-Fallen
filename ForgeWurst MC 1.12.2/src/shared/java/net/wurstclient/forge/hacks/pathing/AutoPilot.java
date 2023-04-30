/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

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

		private Mode(String name, boolean baritone, boolean tesla) {
			this.name = name;
			this.tesla = tesla;
			this.baritone = baritone;
		}

		public String toString() {
			return name;
		}
	}

	private enum ModeType {
		AUTO("Auto", true, false),
		RENDER("Render", false, true);

		private final String name;
		private final boolean auto;
		private final boolean render;

		private ModeType(String name, boolean auto, boolean render) {
			this.name = name;
			this.auto = auto;
			this.render = render;
		}

		public String toString() {
			return name;
		}
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		blockPosArrayList = createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg));
		ChatUtils.message("You can still use this module, but right now it pretty sucks. Still working on it.");
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (modeType.getSelected().auto) {
			mc.player.rotationYaw = (float) getYawAndPitchForPath(mc.player.getPosition(), blockPosArrayList)[0];

			for (int y = -50; y < 50; y++) {
				if (mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTarg, yTarg, zTarg))) {
					setEnabled(false);

					ChatUtils.message("[AUTOPILOT] We have arrived, Disengaging.");
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
				} else if (mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTargA, yTargA, zTargA)) && !(mc.player.getPosition().add(0, y, 0).equals(new BlockPos(xTarg, yTarg, zTarg)))) {
					blockPosArrayList = createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg));
					ChatUtils.message("Starting next section...");
				} else {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
				}
			}
		} else {
			if (mc.player.ticksExisted % 20 == 0) {
				blockPosArrayList = createPath(mc.player.getPosition(), new BlockPos(xTarg, yTarg, zTarg));
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		try {
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
				double lookY = lookVec.y;
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
		} catch (Exception ignored) {
		}
	}

	public static void setTarg(double x, double y, double z) {
		xTarg = x;
		yTarg = y;
		zTarg = z;
	}

	public ArrayList<BlockPos> createPath(BlockPos start, BlockPos target) {
		int numNodesVisited = 0;
		int numNodesConsidered = 0;
		int maxOpenListSize = 0;

		if (start.getDistance(target.getX(), target.getY(), target.getZ()) < mc.gameSettings.renderDistanceChunks * 16) {
			PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, target)));
			HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
			HashMap<BlockPos, Double> gScore = new HashMap<>();
			openList.add(start);
			gScore.put(start, 0.0);

			while (!openList.isEmpty()) {
				BlockPos current = openList.poll();

				numNodesVisited++;
				maxOpenListSize = Math.max(maxOpenListSize, openList.size());

				if (current.equals(target)) {
					// Reconstruct the path
					ArrayList<BlockPos> path = new ArrayList<>();
					path.add(current);
					while (cameFrom.containsKey(current)) {
						current = cameFrom.get(current);
						path.add(0, current);
					}

					if (debug.isChecked()) {
						ChatUtils.message("Path found!");
						ChatUtils.message("Number of nodes visited: " + numNodesVisited);
						ChatUtils.message("Number of nodes considered: " + numNodesConsidered);
						ChatUtils.message("Maximum size of open list: " + maxOpenListSize);
						ChatUtils.message("Length of path: " + path.size());
					} else {
						ChatUtils.message("Path found.");
					}

					return path;
				}

				for (BlockPos neighbor : getNeighbors(current)) {
					double tentativeGScore = gScore.get(current) + getDistance(current, neighbor);
					if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
						cameFrom.put(neighbor, current);
						gScore.put(neighbor, tentativeGScore);
						if (!openList.contains(neighbor)) {
							openList.add(neighbor);
							numNodesConsidered++;
						}
					}
				}
			}
		} else {
			BlockPos blockPos = getClosestSolidBlock(target);
			PriorityQueue<BlockPos> openList = new PriorityQueue<>(Comparator.comparingDouble(pos -> getDistance(pos, blockPos)));
			HashMap<BlockPos, BlockPos> cameFrom = new HashMap<>();
			HashMap<BlockPos, Double> gScore = new HashMap<>();
			openList.add(start);
			gScore.put(start, 0.0);

			xTargA = blockPos.getX();
			yTargA = blockPos.getY();
			zTargA = blockPos.getZ();

			while (!openList.isEmpty()) {
				BlockPos current = openList.poll();

				numNodesVisited++;
				maxOpenListSize = Math.max(maxOpenListSize, openList.size());

				if (current.equals(blockPos)) {
					// Reconstruct the path
					ArrayList<BlockPos> path = new ArrayList<>();
					path.add(current);
					while (cameFrom.containsKey(current)) {
						current = cameFrom.get(current);
						path.add(0, current);
					}

					if (debug.isChecked()) {
						ChatUtils.message("Path found!");
						ChatUtils.message("Number of nodes visited: " + numNodesVisited);
						ChatUtils.message("Number of nodes considered: " + numNodesConsidered);
						ChatUtils.message("Maximum size of open list: " + maxOpenListSize);
						ChatUtils.message("Length of path: " + path.size());
					} else {
						ChatUtils.message("Path found.");
					}

					return path;
				}

				for (BlockPos neighbor : getNeighbors(current)) {
					double tentativeGScore = gScore.get(current) + getDistance(current, neighbor);
					if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
						cameFrom.put(neighbor, current);
						gScore.put(neighbor, tentativeGScore);
						if (!openList.contains(neighbor)) {
							openList.add(neighbor);
							numNodesConsidered++;
						}
					}
				}
			}
		}
		return new ArrayList<>();
	}

	public static BlockPos getClosestSolidBlock(BlockPos targetPos) {
		int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;

		double closestDistance = Double.MAX_VALUE;
		BlockPos closestBlock = null;
		for (int x = mc.player.chunkCoordX - renderDistanceChunks; x <= mc.player.chunkCoordX + renderDistanceChunks; x++) {
			for (int z = mc.player.chunkCoordZ - renderDistanceChunks; z <= mc.player.chunkCoordZ + renderDistanceChunks; z++) {
				for (int y = 0; y <= 256; y++) {
					BlockPos blockPos = new BlockPos(x * 16, y, z * 16);
					double distance = blockPos.distanceSq(targetPos);
					if (distance < closestDistance && !isBlockAboveAir(blockPos) && isBlockAboveAir(blockPos.up()) && isBlockAboveAir(blockPos.up(2))) {
						closestDistance = distance;
						closestBlock = blockPos;
					}
				}
			}
		}
		return closestBlock;
	}

	private static boolean isBlockAboveAir(BlockPos pos) {
		Block block = mc.world.getBlockState(pos).getBlock();
		return block.equals(Blocks.AIR);
	}

	private static ArrayList<BlockPos> getNeighbors(BlockPos pos) {
		ArrayList<BlockPos> neighbors = new ArrayList<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					BlockPos neighbor = pos.add(x, y, z);
					if (isWalkable(neighbor)) {
						neighbors.add(neighbor);
					}
				}
			}
		}
		return neighbors;
	}

	private double getDistance(BlockPos a, BlockPos b) {
		return Math.sqrt(a.distanceSq(b));
	}

	public static boolean isWalkable(BlockPos pos) {
		return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR);
	}

	private double smoothYaw = 0;
	private double smoothPitch = 0;

	public double[] getYawAndPitchForPath(BlockPos playerPos, ArrayList<BlockPos> path) {
		double[] yawAndPitch = new double[]{0, 0};

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

			double xDiff = nextBlock.getX() - playerPos.getX();
			double zDiff = nextBlock.getZ() - playerPos.getZ();
			double yDiff = nextBlock.getY() + 0.5 - (playerPos.getY() + 1.0);
			double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
			double targetYaw = Math.atan2(zDiff, xDiff) * (180 / Math.PI) - 90;
			double targetPitch = Math.atan2(-yDiff, distanceXZ) * (180 / Math.PI);

			double diffYaw = MathHelper.wrapDegrees(targetYaw - smoothYaw);
			double diffPitch = MathHelper.wrapDegrees(targetPitch - smoothPitch);

			// Smooth the values using exponential moving average
			// Adjust this value to control the smoothing
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