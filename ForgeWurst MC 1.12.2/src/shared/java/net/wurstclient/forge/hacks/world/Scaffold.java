package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
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
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class Scaffold extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.LEGIT);

	private final CheckboxSetting jump =
			new CheckboxSetting("Jump", "Scaffold while jumping.",
					false);

	private final CheckboxSetting packets =
			new CheckboxSetting("Packets", "Utilizing packets to help scaffold, Can be buggy on servers.",
					false);

	private double jumpY;
	private float startYaw;

	public Scaffold() {
		super("Scaffold", "Place blocks underneath you automatically.");
		setCategory(Category.WORLD);
		addSetting(mode);
		addSetting(jump);
		addSetting(packets);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			jumpY = mc.player.lastTickPosY;
			startYaw = mc.player.rotationYaw;
		} catch (Exception ignored) {
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		if (mode.getSelected().legit) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
		}
	}

	private Vec3d vec3d;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().normal) {
			Minecraft mc = Minecraft.getMinecraft();
			BlockPos playerBlock = new BlockPos(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);

			if (jump.isChecked()) {
				playerBlock = new BlockPos(playerBlock.getX(), jumpY, playerBlock.getZ());
			}
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
		} else if (mode.getSelected().legit) {
			if (!mc.gameSettings.keyBindJump.isKeyDown()) {mc.player.rotationYaw = startYaw - 180;
				mc.player.rotationPitch = 82;

				if (mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 1, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);

					mc.player.rotationYaw = startYaw - 180;
				} else {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
					mc.player.rotationYaw = startYaw;
				}

				vec3d = new Vec3d(Math.round(mc.player.lastTickPosX), mc.player.lastTickPosY, Math.round(mc.player.lastTickPosZ));
			} else {
				mc.player.rotationPitch = 90;
				float[] rot = RotationUtils.getNeededRotations(new Vec3d(Math.round(vec3d.x), mc.player.lastTickPosY - 1, Math.round(vec3d.z)).addVector(0.5, 0, 0.5));
				mc.player.rotationYaw = rot[0];
				if (mc.player.onGround) {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, true);
				} else {
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, true);
					KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
				}
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
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
					mc.player.swingArm(EnumHand.MAIN_HAND);
					mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
					mc.player.inventory.currentItem = last;
					mc.playerController.updateController();
				}
			}

			double var4 = pos.getX() + 0.25D - mc.player.posX;
			double var6 = pos.getZ() + 0.25D - mc.player.posZ;
			double var8 = pos.getY() + 0.25D - (mc.player.posY + mc.player.getEyeHeight());
			double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
			double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
			double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yaw, (float) pitch, mc.player.onGround));

			if (packets.isChecked()) {
				mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, face, EnumHand.MAIN_HAND, (float) var4, (float) var6, (float) var8));
			}
		}

		if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
			mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
			mc.player.swingArm(EnumHand.MAIN_HAND);

			double var4 = pos.getX() + 0.25D - mc.player.posX;
			double var6 = pos.getZ() + 0.25D - mc.player.posZ;
			double var8 = pos.getY() + 0.25D - (mc.player.posY + mc.player.getEyeHeight());
			double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
			double yaw = (float) (Math.atan2(var6, var4) * 180.0D / Math.PI) - 90.0F;
			double pitch = (float) -(Math.atan2(var8, var14) * 180.0D / Math.PI);
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation((float) yaw, (float) pitch, mc.player.onGround));

			if (packets.isChecked()) {
				mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, face, EnumHand.MAIN_HAND, (float) var4, (float) var6, (float) var8));
			}
		}
	}

	private enum Mode {
		NORMAL("Normal", true, false),
		LEGIT("Legit", false, true);

		private final String name;
		private final boolean normal;
		private final boolean legit;

		private Mode(String name, boolean normal, boolean legit) {
			this.name = name;
			this.normal = normal;
			this.legit = legit;
		}

		public String toString() {
			return name;
		}
	}
}