package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;

public final class Scaffold extends Hack {

	private final EnumSetting<RotationsMode> rotationMode =
			new EnumSetting<>("RotationMode", RotationsMode.values(), RotationsMode.A);

	private final CheckboxSetting swing =
			new CheckboxSetting("Swing", "Should we swing the arm when placing?.",
					false);

	private final CheckboxSetting tower =
			new CheckboxSetting("Tower", "Should we tower up fast?.",
					false);

	public Scaffold() {
		super("Scaffold", "Place blocks underneath you automatically.");
		setCategory(Category.WORLD);
		addSetting(rotationMode);
		addSetting(swing);
		addSetting(tower);
	}

	private enum RotationsMode {
		A("A"),
		B("B");

		private final String name;

		private RotationsMode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
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

		if (tower.isChecked()) {
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
				if (!mc.player.onGround && mc.player.posY - Math.floor(mc.player.posY) <= 0.1) {
					mc.player.motionY = 0.41999998688697815;
				}
			}
		}
	}

	private static boolean isValidBlock(BlockPos blockPos) {
		return !(mc.world.isAirBlock(blockPos));
	}

	private void place(BlockPos pos, EnumFacing face) {
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

		if (!(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock)) {
			for (int i = 0; i < 9; i++) {
				ItemStack item = mc.player.inventory.getStackInSlot(i);
				if (item.getItem() instanceof ItemBlock) {
					int last = mc.player.inventory.currentItem;
					mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
					mc.player.inventory.currentItem = i;
					mc.playerController.updateController();
					mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
					if (swing.isChecked()) {
						mc.player.swingArm(EnumHand.MAIN_HAND);
					} else {
						mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
					}
					mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
					mc.player.inventory.currentItem = last;
					mc.playerController.updateController();
				}
			}

			if (rotationMode.getSelected() == RotationsMode.A) {
				double var4 = pos.getX() + 0.25D - mc.player.posX;
				double var6 = pos.getZ() + 0.25D - mc.player.posZ;
				double var8 = pos.getY() + 0.25D - (mc.player.posY + mc.player.getEyeHeight());
				double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
				double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
				double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yaw, (float) pitch, mc.player.onGround));
			} else if (rotationMode.getSelected() == RotationsMode.B) {
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(getRotationsBlock(pos, face)[0], getRotationsBlock(pos, face)[1], mc.player.onGround));
			}
		}

		if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
			if (swing.isChecked()) {
				mc.player.swingArm(EnumHand.MAIN_HAND);
			} else {
				mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
			}

			if (rotationMode.getSelected() == RotationsMode.A) {
				double var4 = pos.getX() + 0.25D - mc.player.posX;
				double var6 = pos.getZ() + 0.25D - mc.player.posZ;
				double var8 = pos.getY() + 0.25D - (mc.player.posY + mc.player.getEyeHeight());
				double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
				double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
				double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yaw, (float) pitch, mc.player.onGround));
			} else if (rotationMode.getSelected() == RotationsMode.B) {
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(getRotationsBlock(pos, face)[0], getRotationsBlock(pos, face)[1], mc.player.onGround));
			}
		}
	}

	public float[] getRotationsBlock(BlockPos block, EnumFacing face) {
		double x = (double)block.getX() + 0.5 - mc.player.posX + (double)face.getFrontOffsetX() / 2.0;
		double z = (double)block.getZ() + 0.5 - mc.player.posZ + (double)face.getFrontOffsetZ() / 2.0;
		double y = (double)block.getY() + 0.5;
		double d1 = mc.player.posY + (double)mc.player.getEyeHeight() - y;
		double d3 = MathHelper.sqrt(x * x + z * z);
		float yaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
		float pitch = (float)(Math.atan2(d1, d3) * 180.0 / 3.141592653589793);
		if (yaw < 0.0f) {
			yaw += 360.0f;
		}
		return new float[]{yaw, pitch};
	}
}