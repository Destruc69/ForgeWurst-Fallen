/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WPlayerController;
import net.wurstclient.forge.compatibility.WVec3d;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BlockUtils
{

	public static List<Block> blacklistedBlocks = Arrays.asList(Blocks.AIR, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA,
			Blocks.ENCHANTING_TABLE, Blocks.CARPET, Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE, Blocks.IRON_BARS,
			Blocks.SNOW_LAYER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.COAL_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE,
			Blocks.CHEST, Blocks.TORCH, Blocks.ANVIL, Blocks.TRAPPED_CHEST, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.TNT,
			Blocks.GOLD_ORE, Blocks.IRON_ORE, Blocks.LAPIS_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.QUARTZ_ORE, Blocks.REDSTONE_ORE,
			Blocks.WOODEN_PRESSURE_PLATE, Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
			Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER);

	private static final Minecraft mc = Minecraft.getMinecraft();
	
	public static IBlockState getState(BlockPos pos)
	{
		return WMinecraft.getWorld().getBlockState(pos);
	}
	
	public static Block getBlock(BlockPos pos)
	{
		return getState(pos).getBlock();
	}
	
	public static int getId(BlockPos pos)
	{
		return Block.getIdFromBlock(getBlock(pos));
	}
	
	public static String getName(Block block)
	{
		return "" + Block.REGISTRY.getNameForObject(block);
	}
	
	public static Material getMaterial(BlockPos pos)
	{
		return getState(pos).getMaterial();
	}
	
	public static AxisAlignedBB getBoundingBox(BlockPos pos)
	{
		return getState(pos).getBoundingBox(WMinecraft.getWorld(), pos)
			.offset(pos);
	}
	
	public static boolean canBeClicked(BlockPos pos)
	{
		return getBlock(pos).canCollideCheck(getState(pos), false);
	}
	
	public static float getHardness(BlockPos pos)
	{
		return getState(pos).getPlayerRelativeBlockHardness(
			WMinecraft.getPlayer(), WMinecraft.getWorld(), pos);
	}
	
	public static void placeBlockSimple(BlockPos pos)
	{
		EnumFacing side = null;
		EnumFacing[] sides = EnumFacing.values();
		
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
		
		Vec3d[] hitVecs = new Vec3d[sides.length];
		for(int i = 0; i < sides.length; i++)
			hitVecs[i] =
				posVec.add(new Vec3d(sides[i].getDirectionVec()).scale(0.5));
		
		for(int i = 0; i < sides.length; i++)
		{
			// check if neighbor can be right clicked
			if(!canBeClicked(pos.offset(sides[i])))
				continue;
			
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false,
				true, false) != null)
				continue;
			
			side = sides[i];
			break;
		}
		
		if(side == null)
			for(int i = 0; i < sides.length; i++)
			{
				// check if neighbor can be right clicked
				if(!canBeClicked(pos.offset(sides[i])))
					continue;
				
				// check if side is facing away from player
				if(distanceSqPosVec > eyesPos.squareDistanceTo(hitVecs[i]))
					continue;
				
				side = sides[i];
				break;
			}
		
		if(side == null)
			return;
		
		Vec3d hitVec = hitVecs[side.ordinal()];
		
		// face block
		RotationUtils.faceVectorPacket(hitVec);
		if(RotationUtils.getAngleToLastReportedLookVec(hitVec) > 1)
			return;
		
		// check timer
		try
		{
			Field rightClickDelayTimer = mc.getClass()
				.getDeclaredField(ForgeWurst.getForgeWurst().isObfuscated()
					? "field_71467_ac" : "rightClickDelayTimer");
			rightClickDelayTimer.setAccessible(true);
			
			if(rightClickDelayTimer.getInt(mc) > 0)
				return;
			
		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
		
		// place block
		WPlayerController.processRightClickBlock(pos.offset(side),
			side.getOpposite(), hitVec);
		
		// swing arm
		WMinecraft.getPlayer().connection
			.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
		
		// reset timer
		try
		{
			Field rightClickDelayTimer = mc.getClass()
				.getDeclaredField(ForgeWurst.getForgeWurst().isObfuscated()
					? "field_71467_ac" : "rightClickDelayTimer");
			rightClickDelayTimer.setAccessible(true);
			rightClickDelayTimer.setInt(mc, 4);
			
		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static boolean breakBlockSimple(BlockPos pos)
	{
		EnumFacing side = null;
		EnumFacing[] sides = EnumFacing.values();
		
		Vec3d eyesPos = RotationUtils.getEyesPos();
		Vec3d relCenter = getState(pos)
			.getBoundingBox(WMinecraft.getWorld(), pos).getCenter();
		Vec3d center = new Vec3d(pos).add(relCenter);
		
		Vec3d[] hitVecs = new Vec3d[sides.length];
		for(int i = 0; i < sides.length; i++)
		{
			Vec3i dirVec = sides[i].getDirectionVec();
			Vec3d relHitVec = new Vec3d(WVec3d.getX(relCenter) * dirVec.getX(),
				WVec3d.getY(relCenter) * dirVec.getY(),
				WVec3d.getZ(relCenter) * dirVec.getZ());
			hitVecs[i] = center.add(relHitVec);
		}
		
		for(int i = 0; i < sides.length; i++)
		{
			// check line of sight
			if(WMinecraft.getWorld().rayTraceBlocks(eyesPos, hitVecs[i], false,
				true, false) != null)
				continue;
			
			side = sides[i];
			break;
		}
		
		if(side == null)
		{
			double distanceSqToCenter = eyesPos.squareDistanceTo(center);
			for(int i = 0; i < sides.length; i++)
			{
				// check if side is facing towards player
				if(eyesPos.squareDistanceTo(hitVecs[i]) >= distanceSqToCenter)
					continue;
				
				side = sides[i];
				break;
			}
		}
		
		// player is inside of block, side doesn't matter
		if(side == null)
			side = sides[0];
		
		// face block
		RotationUtils.faceVectorPacket(hitVecs[side.ordinal()]);
		
		// damage block
		if(!mc.playerController.onPlayerDamageBlock(pos, side))
			return false;
		
		// swing arm
		WMinecraft.getPlayer().connection
			.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
		
		return true;
	}
	
	public static void breakBlocksPacketSpam(Iterable<BlockPos> blocks)
	{
		Vec3d eyesPos = RotationUtils.getEyesPos();
		NetHandlerPlayClient connection = WMinecraft.getPlayer().connection;
		
		for(BlockPos pos : blocks)
		{
			Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
			
			for(EnumFacing side : EnumFacing.values())
			{
				Vec3d hitVec =
					posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
				
				// check if side is facing towards player
				if(eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec)
					continue;
				
				// break block
				connection.sendPacket(new CPacketPlayerDigging(
					Action.START_DESTROY_BLOCK, pos, side));
				connection.sendPacket(new CPacketPlayerDigging(
					Action.STOP_DESTROY_BLOCK, pos, side));
				
				break;
			}
		}
	}

	public static final Block[] NON_NATURAL_BLOCKS = {
			Blocks.BARRIER,
			Blocks.BEACON,
			Blocks.BED,
			Blocks.BREWING_STAND,
			Blocks.CHEST,
			Blocks.COMMAND_BLOCK,
			Blocks.DISPENSER,
			Blocks.DROPPER,
			Blocks.ENCHANTING_TABLE,
			Blocks.ENDER_CHEST,
			Blocks.END_PORTAL_FRAME,
			Blocks.FURNACE,
			Blocks.HOPPER,
			Blocks.JUKEBOX,
			Blocks.LADDER,
			Blocks.PORTAL,
			Blocks.NOTEBLOCK,
			Blocks.OBSERVER,
			Blocks.PISTON,
			Blocks.BLACK_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX,
			Blocks.STANDING_SIGN,
			Blocks.WALL_SIGN,
	};

	public static ArrayList<BlockPos> getManMadeObstructions(ArrayList<BlockPos> blockPosArrayList) {
		ArrayList<BlockPos> tamperedBlocks = new ArrayList<>();

		for (BlockPos blockPos : blockPosArrayList) {
			if (isBlockManMade(blockPos)) {
				tamperedBlocks.add(blockPos);
			}
		}

		return tamperedBlocks;
	}

	private static boolean isBlockManMade(BlockPos blockPos) {
		for (Block block : NON_NATURAL_BLOCKS) {
			return mc.world.getBlockState(blockPos).getBlock().equals(block);
		}
		return false;
	}

	public static ArrayList<BlockPos> getBlockPosWithinRenderDistance(EntityPlayer player) {
		int renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16; // Convert chunks to blocks
		BlockPos playerPos = player.getPosition();

		int minX = playerPos.getX() - renderDistance;
		int minY = playerPos.getY() - renderDistance;
		int minZ = playerPos.getZ() - renderDistance;
		int maxX = playerPos.getX() + renderDistance;
		int maxY = playerPos.getY() + renderDistance;
		int maxZ = playerPos.getZ() + renderDistance;

		int arraySize = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
		BlockPos[] blockPosArray = new BlockPos[arraySize];

		int index = 0;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					blockPosArray[index++] = new BlockPos(x, y, z);
				}
			}
		}

		return new ArrayList<>(Arrays.asList(blockPosArray));
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

	public static boolean canPlayerSeeBlockPos(BlockPos targetPos) {
		EntityPlayer player = mc.player;
		Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d end = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5); // Center of the target BlockPos

		RayTraceResult result = mc.world.rayTraceBlocks(start, end);

		return result != null && result.typeOfHit == RayTraceResult.Type.BLOCK;
	}

	public static BlockPos findNearestBlockPos(BlockPos center, Block targetBlock) {
		// 1. Reduced search radius (adjust as needed)
		int searchRadius = 16; // Example: reduced from render distance

		BlockPos nearestBlockPos = null;
		double nearestDistanceSq = Double.MAX_VALUE;

		// 2. Optimized loop structure
		IBlockState cachedState;
		for (int x = -searchRadius; x <= searchRadius; x++) {
			for (int z = -searchRadius; z <= searchRadius; z++) { // Loop through X and Z first
				for (int y = -searchRadius; y <= searchRadius; y++) {
					BlockPos pos = center.add(x, y, z);

					// 3. Cached BlockState
					cachedState = mc.world.getBlockState(pos);
					if (cachedState.getBlock() == targetBlock) {
						double distanceSq = center.distanceSq(pos);

						if (distanceSq < nearestDistanceSq) {
							nearestDistanceSq = distanceSq;
							nearestBlockPos = pos;
							break; // Break early if found
						}
					}
				}
			}
		}

		return nearestBlockPos;
	}

	public static List<BlockPos> findAllBlockPos(BlockPos center, Block targetBlock, int searchRadius) {
		List<BlockPos> allBlockPosList = new ArrayList<>();

		// Optimized loop structure
		IBlockState cachedState;
		for (int x = -searchRadius; x <= searchRadius; x++) {
			for (int z = -searchRadius; z <= searchRadius; z++) { // Loop through X and Z first
				for (int y = -searchRadius; y <= searchRadius; y++) {
					BlockPos pos = center.add(x, y, z);

					// Cached BlockState
					cachedState = mc.world.getBlockState(pos);
					if (cachedState.getBlock() == targetBlock) {
						allBlockPosList.add(pos);
					}
				}
			}
		}

		return allBlockPosList;
	}

}
