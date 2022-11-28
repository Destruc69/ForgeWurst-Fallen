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
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.world.Scaffold;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;

import java.util.ArrayList;

public final class HighwayBuilder extends Hack {

	private final SliderSetting rotstrength =
			new SliderSetting("RotationStrength", "Rotation strength", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);


	private final CheckboxSetting replace =
			new CheckboxSetting("Replace", "Path must be the block your holding.",
					false);

	private final CheckboxSetting clear =
			new CheckboxSetting("Clear", "Clears the path.",
					false);
	public static ArrayList<BlockPos> BlockArray = new ArrayList<>();
	public static ArrayList<BlockPos> clearArray = new ArrayList<>();
	public static double startYaw;
	public static BlockPos targPos;

	public HighwayBuilder() {
		super("HighwayBuilder", "Builds highways.");
		setCategory(Category.PATHING);
		addSetting(rotstrength);
		addSetting(replace);
		addSetting(clear);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			startYaw = Math.round(mc.player.rotationYaw);
			BlockArray.clear();
			clearArray.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		BlockArray.clear();
		clearArray.clear();
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			for (BlockPos blockPos : BlockArray) {
				if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
					targPos = blockPos;
				}
			}
			if (PlayerUtils.CanSeeBlock(targPos)) {
				mc.playerController.processRightClickBlock(mc.player, mc.world, targPos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
				mc.player.swingArm(EnumHand.MAIN_HAND);
			}

			if (clear.isChecked()) {
				for (BlockPos blockPos : clearArray) {
					mc.playerController.onPlayerDamageBlock(blockPos, mc.player.getHorizontalFacing());
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}

			if (replace.isChecked()) {
				for (BlockPos blockPos : BlockArray) {
					if (!(mc.world.getBlockState(blockPos).getBlock().equals(Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem())))) {
						mc.playerController.onPlayerDamageBlock(blockPos, mc.player.getHorizontalFacing());
						mc.player.swingArm(EnumHand.MAIN_HAND);
						for (int a = 0; a < rotstrength.getValueF(); a++) {
							RotationUtils.faceVectorPacket(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
						}
					}
				}
			}

			for (BlockPos blockPos : clearArray) {
				for (int a = 0; a < rotstrength.getValueF(); a++) {
					RotationUtils.faceVectorPacket(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
				}
			}

			for (BlockPos blockPos : BlockArray) {
				float[] angle = MathUtils.calcAngle(Scaffold.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double) ((float) blockPos.getX() + 0.5f), (double) ((float) blockPos.getY() - 0.5f), (double) ((float) blockPos.getZ() + 0.5f)));
				double yawForRot = angle[0];
				double pitchForRot = (float) MathHelper.normalizeAngle((int) ((int) angle[1]), (int) 360);
				for (int a = 0; a < rotstrength.getValueF(); a++) {
					mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yawForRot, (float) pitchForRot, mc.player.onGround));
				}
			}

			mc.player.rotationYaw = (int) startYaw;
			if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock().equals(Blocks.AIR)) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
				mc.player.setVelocity(0, 0, 0);
			} else {
				if (mc.world.getBlockState(BlockArray.get(0)).getBlock().equals(Blocks.OBSIDIAN) &&
						mc.world.getBlockState(BlockArray.get(1)).getBlock().equals(Blocks.OBSIDIAN) &&
						mc.world.getBlockState(BlockArray.get(2)).getBlock().equals(Blocks.OBSIDIAN) &&
						mc.world.getBlockState(BlockArray.get(3)).getBlock().equals(Blocks.OBSIDIAN) &&
						mc.world.getBlockState(BlockArray.get(4)).getBlock().equals(Blocks.OBSIDIAN) &&
						mc.world.getBlockState(BlockArray.get(5)).getBlock().equals(Blocks.OBSIDIAN)) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void tick(WUpdateEvent event) {
		//Taken from SpiderMod
		BlockPos orignPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
		EnumFacing enumFacing = mc.player.getHorizontalFacing();
		if (enumFacing == EnumFacing.EAST) {
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down().north());
			BlockArray.add(orignPos.down().south());
			BlockArray.add(orignPos.down().north().north());
			BlockArray.add(orignPos.down().south().south());
			BlockArray.add(orignPos.down().north().north().north());
			BlockArray.add(orignPos.down().south().south().south());
			BlockArray.add(orignPos.down().north().north().north().up());
			BlockArray.add(orignPos.down().south().south().south().up());
		}
		if (enumFacing == EnumFacing.NORTH) {
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down().east());
			BlockArray.add(orignPos.down().west());
			BlockArray.add(orignPos.down().east().east());
			BlockArray.add(orignPos.down().west().west());
			BlockArray.add(orignPos.down().east().east().east());
			BlockArray.add(orignPos.down().west().west().west());
			BlockArray.add(orignPos.down().east().east().east().up());
			BlockArray.add(orignPos.down().west().west().west().up());
		}
		if (enumFacing == EnumFacing.SOUTH) {
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down().east());
			BlockArray.add(orignPos.down().west());
			BlockArray.add(orignPos.down().east().east());
			BlockArray.add(orignPos.down().west().west());
			BlockArray.add(orignPos.down().east().east().east());
			BlockArray.add(orignPos.down().west().west().west());
			BlockArray.add(orignPos.down().east().east().east().up());
			BlockArray.add(orignPos.down().west().west().west().up());
		}
		if (enumFacing == EnumFacing.WEST) {
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down());
			BlockArray.add(orignPos.down().north());
			BlockArray.add(orignPos.down().south());
			BlockArray.add(orignPos.down().north().north());
			BlockArray.add(orignPos.down().south().south());
			BlockArray.add(orignPos.down().north().north().north());
			BlockArray.add(orignPos.down().south().south().south());
			BlockArray.add(orignPos.down().north().north().north().up());
			BlockArray.add(orignPos.down().south().south().south().up());
		}
		Vec3d pos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
		if (mc.player.getHorizontalFacing().equals(EnumFacing.EAST)) {
			BlockPos interpPos = new BlockPos(pos.x, pos.y, pos.z).east().east();
			for (int l_X = -2; l_X <= 2; ++l_X) {
				for (int l_Y = -0; l_Y <= 3; ++l_Y) {
					clearArray.add(interpPos.add(0, l_Y, l_X));
				}
			}
		}
		if (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH)) {
			BlockPos interpPos = new BlockPos(pos.x, pos.y, pos.z).north().north();

			for (int l_X = -2; l_X <= 2; ++l_X) {
				for (int l_Y = -0; l_Y <= 3; ++l_Y) {
					clearArray.add(interpPos.add(l_X, l_Y, 0));
				}
			}
		}
		if (mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH)) {
			BlockPos interpPos = new BlockPos(pos.x, pos.y, pos.z).south().south();

			for (int l_X = -2; l_X <= 2; ++l_X) {
				for (int l_Y = -0; l_Y <= 3; ++l_Y) {
					clearArray.add(interpPos.add(l_X, l_Y, 0));
				}
			}
		}
		if (mc.player.getHorizontalFacing().equals(EnumFacing.WEST)) {
			BlockPos interpPos = new BlockPos(pos.x, pos.y, pos.z).west().west();

			for (int l_X = -2; l_X <= 2; ++l_X) {
				for (int l_Y = -0; l_Y <= 3; ++l_Y) {
					clearArray.add(interpPos.add(0, l_Y, l_X));
				}
			}
		}

		if (mc.player.ticksExisted % 10 == 0) {
			BlockArray.clear();
			clearArray.clear();
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		for (BlockPos blockPos : BlockArray) {
			FallenRenderUtils.renderPosFilled(blockPos, event.getPartialTicks(), 1, 0, 0, (float) 1 / 2);
		}
		for (BlockPos blockPos : clearArray) {
			FallenRenderUtils.renderPosFilled(blockPos, event.getPartialTicks(), 1, 0, 0, (float) 1 / 2);
		}
	}
}