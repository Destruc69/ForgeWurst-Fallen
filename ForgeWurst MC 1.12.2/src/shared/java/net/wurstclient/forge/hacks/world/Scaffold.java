package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.movement.AutoSneak;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RotationUtils;

import java.lang.reflect.Field;

public final class Scaffold extends Hack {

	public Scaffold() {
		super("Scaffold", "Place blocks underneath you automatically.");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		BlockPos playerBlock = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);
		if (mc.world.isAirBlock(playerBlock.add(0, -1, 0))) {
			if (isValidBlock(playerBlock.add(0, -2, 0))) {
				place(playerBlock.add(0, -1, 0), EnumFacing.UP);
			} else if (isValidBlock(playerBlock.add(-1, -1, 0))) {
				place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
			} else if (isValidBlock(playerBlock.add(1, -1, 0))) {
				place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
			} else if (isValidBlock(playerBlock.add(0, -1, -1))) {
				place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
			} else if (isValidBlock(playerBlock.add(0, -1, 1))) {
				place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
			} else if (isValidBlock(playerBlock.add(1, -1, 1))) {
				if (isValidBlock(playerBlock.add(0, -1, 1))) {
					place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
				}
				place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
			} else if (isValidBlock(playerBlock.add(-1, -1, 1))) {
				if (isValidBlock(playerBlock.add(-1, -1, 0))) {
					place(playerBlock.add(0, -1, 1), EnumFacing.WEST);
				}
				place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
			} else if (isValidBlock(playerBlock.add(-1, -1, -1))) {
				if (isValidBlock(playerBlock.add(0, -1, -1))) {
					place(playerBlock.add(0, -1, -1), EnumFacing.SOUTH);
				}
				place(playerBlock.add(-1, -1, -1), EnumFacing.WEST);
			} else if (isValidBlock(playerBlock.add(1, -1, -1))) {
				if (isValidBlock(playerBlock.add(1, -1, 0))) {
					place(playerBlock.add(1, -1, 0), EnumFacing.EAST);
				}
				place(playerBlock.add(1, -1, -1), EnumFacing.NORTH);
			}
		}
	}

	private static boolean isValidBlock(BlockPos blockPos) {
		return !(mc.world.isAirBlock(blockPos));
	}

	private void place(BlockPos pos, EnumFacing face) {
		double offsetX = 0.5D;
		double offsetY = 0.5D;
		double offsetZ = 0.5D;

		if (face == EnumFacing.UP) {
			pos = pos.add(0, -1, 0);
		} else if (face == EnumFacing.NORTH) {
			pos = pos.add(0, 0, 1);
		} else if (face == EnumFacing.EAST) {
			pos = pos.add(-1, 0, 0);
		} else if (face == EnumFacing.SOUTH) {
			pos = pos.add(0, 0, -1);
		} else if (face == EnumFacing.WEST) {
			pos = pos.add(1, 0, 0);
		}

		ItemStack heldItem = mc.player.getHeldItem(EnumHand.MAIN_HAND);
		if (!(heldItem.getItem() instanceof ItemBlock)) {
			for (int i = 0; i < 9; i++) {
				ItemStack item = mc.player.inventory.getStackInSlot(i);
				if (item.getItem() instanceof ItemBlock) {
					int lastSlot = mc.player.inventory.currentItem;
					mc.player.inventory.currentItem = i;
					mc.playerController.updateController();
					mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(offsetX, offsetY, offsetZ), EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);
					mc.player.inventory.currentItem = lastSlot;
					mc.playerController.updateController();
				}
			}
		} else {
			mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(offsetX, offsetY, offsetZ), EnumHand.MAIN_HAND);
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}

		double playerX = mc.player.posX;
		double playerY = mc.player.posY + mc.player.getEyeHeight();
		double playerZ = mc.player.posZ;
		double deltaX = pos.getX() + offsetX - playerX;
		double deltaY = pos.getY() + offsetY - playerY;
		double deltaZ = pos.getZ() + offsetZ - playerZ;
		double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0;
		double pitch = Math.toDegrees(Math.atan2(-deltaY, horizontalDistance));
		mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yaw, (float) pitch, mc.player.onGround));
	}
}