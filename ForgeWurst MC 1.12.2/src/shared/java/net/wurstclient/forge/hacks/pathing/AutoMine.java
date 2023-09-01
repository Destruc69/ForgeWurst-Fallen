/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.LandPathUtils;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public final class AutoMine extends Hack {

	private boolean isStarted;

	private BlockPos posA;
	private BlockPos posB;

	private ArrayList<BlockPos> blockPosArrayList;

	private int save;

	public AutoMine() {
		super("AutoMine", "Automatically mines blocks.");
		setCategory(Category.PATHING);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		isStarted = false;
		blockPosArrayList = new ArrayList<>();
		posA = new BlockPos(0, 0, 0);
		posB = new BlockPos(0, 0, 0);
		save = 0;

		try {
			ChatUtils.message("[AutoMine] Right click Pos A and B.");
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	private ArrayList<BlockPos> path;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		BlockPos blockPos = mc.objectMouseOver.getBlockPos();

		if (!isStarted) {
			if (mc.gameSettings.keyBindAttack.isKeyDown()) {
				if (posA.getY() == 0 && posB.getY() == 0) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindAttack, false);
					posA = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
					ChatUtils.message("Pos A: " + posA);
				} else if (posA.getY() != 0 && posB.getY() == 0) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindAttack, false);
					posB = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
					ChatUtils.message("Pos B: " + posB);
				}
			}

			if (posA.getY() != 0 && posB.getY() != 0) {
				if (!posA.equals(posB)) {
					blockPosArrayList = getAllBlocksBetween(posA, posB);
					ChatUtils.message("Okay, Engaging!");
					isStarted = true;
				} else {
					ChatUtils.message("Pos A and Pos B cannot be the same pos!");
					posA = new BlockPos(0, 0, 0);
					posB = new BlockPos(0, 0, 0);
					blockPosArrayList.clear();
				}
			}
		} else {
			try {
				ArrayList<BlockPos> blockPosArraySorted = sortBlockPosByY(blockPosArrayList);

				blockPosArrayList.removeIf(blockPos1 -> mc.world.getBlockState(blockPos1).getBlock().equals(Blocks.AIR));

				BlockPos targPos = blockPosArraySorted.get(0);

				int percentage = (blockPosArrayList.size() * 100) / getAllBlocksBetween(posA, posB).size();
				if (mc.isSingleplayer()) {
					mc.ingameGUI.setOverlayMessage(blockPosArraySorted.size() + "/" + getAllBlocksBetween(posA, posB).size() + " | " + percentage + "%", true);
				} else {
					if (save != percentage) {
						save = percentage;
						ChatUtils.message(blockPosArraySorted.size() + "/" + getAllBlocksBetween(posA, posB).size() + " | " + percentage + "%");
					}
				}
				if (percentage <= 0) {
					ChatUtils.message("[AutoMine] Completed");
					setEnabled(false);
				}

				if (targPos != null) {
					if (mc.player.getDistance(targPos.getX(), targPos.getY(), targPos.getZ()) > 3) {
						if (mc.player.onGround) {
							if (mc.player.ticksExisted % 20 == 0) {
								path = LandPathUtils.createPath(mc.player.getPosition().add(0, -1, 0), targPos, PathfinderModule.debug.isChecked());
							}

							double[] toMove = LandPathUtils.calculateMotion(blockPosArrayList, mc.player.rotationYaw, LandPathUtils.isYawStable(mc.player.rotationYaw));
							mc.player.motionX = toMove[0];
							mc.player.motionZ = toMove[1];

							KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, mc.player.onGround && mc.player.collidedHorizontally || mc.player.isInWater() && !mc.player.collidedHorizontally);

							KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
							KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, true);
						}
					} else {
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
						KeyBindingUtils.setPressed(mc.gameSettings.keyBindSprint, false);
						mc.playerController.onPlayerDamageBlock(targPos, EnumFacing.DOWN);
						mc.player.swingArm(EnumHand.MAIN_HAND);

						double[] toMove = LandPathUtils.calculateMotion(blockPosArrayList, Math.toRadians(mc.player.rotationYaw), LandPathUtils.isYawStable(mc.player.rotationYaw));
						mc.player.motionX = toMove[0];
						mc.player.motionZ = toMove[1];

						if (mc.player.onGround && mc.player.collidedHorizontally || mc.player.isInWater() && !mc.player.collidedHorizontally) {
							mc.player.jump();
						}
					}
				}
			} catch (Exception ignored) {
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(2);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glPushMatrix();
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
				-TileEntityRendererDispatcher.staticPlayerY,
				-TileEntityRendererDispatcher.staticPlayerZ);

		if (isStarted) {
			for (BlockPos blockPos : blockPosArrayList) {
				GL11.glColor4f(0, 1, 0, 0.5F);
				GL11.glBegin(GL11.GL_LINES);
				RenderUtils.drawOutlinedBox(Objects.requireNonNull(BlockUtils.getBoundingBox(blockPos)));
				GL11.glEnd();
			}
		}

		GL11.glPopMatrix();

		// GL resets
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);

		if (path != null) {
			if (path.size() > 0) {
				LandPathUtils.render(PathfinderModule.isRenderTesla(), path, PathfinderModule.lineWidth.getValueI(), PathfinderModule.pathRed.getValueF(), PathfinderModule.pathGreen.getValueF(), PathfinderModule.pathBlue.getValueF());
			}
		}
	}

	public static ArrayList<BlockPos> getAllBlocksBetween(BlockPos posA, BlockPos posB) {
		ArrayList<BlockPos> blockPosList = new ArrayList<>();

		int minX = Math.min(posA.getX(), posB.getX());
		int minY = Math.min(posA.getY(), posB.getY());
		int minZ = Math.min(posA.getZ(), posB.getZ());
		int maxX = Math.max(posA.getX(), posB.getX());
		int maxY = Math.max(posA.getY(), posB.getY());
		int maxZ = Math.max(posA.getZ(), posB.getZ());

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					blockPosList.add(new BlockPos(x, y, z));
				}
			}
		}

		return blockPosList;
	}

	private ArrayList<BlockPos> sortBlockPosByY(ArrayList<BlockPos> blockPosList) {
		if (mc.player == null) {
			// Handle null cases
			return new ArrayList<>();
		}

		// Create a copy of the original ArrayList to avoid modifying the original list
		ArrayList<BlockPos> sortedList = new ArrayList<>(blockPosList);

		// Get the player
		EntityPlayer player = mc.player;

		// Sort the list using a custom comparator based on Y positions and proximity to the player
		sortedList.sort(new Comparator<BlockPos>() {
			@Override
			public int compare(BlockPos pos1, BlockPos pos2) {
				World world = player.world;
				Block block1 = pos1.getY() >= 0 ? world.getBlockState(pos1).getBlock() : Blocks.AIR;
				Block block2 = pos2.getY() >= 0 ? world.getBlockState(pos2).getBlock() : Blocks.AIR;

				// Handle air blocks
				if (block1 == Blocks.AIR && block2 == Blocks.AIR) {
					return 0;
				} else if (block1 == Blocks.AIR) {
					return 1;
				} else if (block2 == Blocks.AIR) {
					return -1;
				}

				// Compare Y positions
				int compareByY = Integer.compare(pos2.getY(), pos1.getY());
				if (compareByY != 0) {
					return compareByY;
				}

				// Compare by squared distance
				double distanceToPos1 = pos1.distanceSq(player.posX, player.posY, player.posZ);
				double distanceToPos2 = pos2.distanceSq(player.posX, player.posY, player.posZ);
				return Double.compare(distanceToPos1, distanceToPos2);
			}
		});

		return sortedList;
	}
}