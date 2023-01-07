package net.wurstclient.forge.hacks.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.*;

import java.awt.*;

public final class Scaffold extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	public static double startYaw = 0;


	public static float yawForRot;
	public static float pitchForRot;

	private enum Mode {
		NORMAL("Normal", true, false, false, false, false),
		BASIC("Basic", false, false, true, false, false),
		LEGIT("Legit", false, true, false, false, false),
		FALLEN("Fallen", false, false, false, true, false),
		FILL("Fill", false, false, false, false, true);

		private final String name;
		private final boolean normal;
		private final boolean legit;
		private final boolean basic;
		private final boolean fallen;
		private final boolean fill;

		private Mode(String name, boolean normal, boolean legit, boolean basic, boolean fallen, boolean fill) {
			this.name = name;
			this.normal = normal;
			this.legit = legit;
			this.basic = basic;
			this.fallen = fallen;
			this.fill = fill;
		}

		public String toString() {
			return name;
		}
	}

	public Scaffold() {
		super("Scaffold", "thank you phobos my beloved");
		setCategory(Category.WORLD);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		try {
			startYaw = mc.player.rotationYaw;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		startYaw = 0;
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.isSwingInProgress) {
			mc.player.motionX = mc.player.motionX / 2;
			mc.player.motionZ = mc.player.motionZ / 2;
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
		} else {
			mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
		}
		if (mode.getSelected().normal || mode.getSelected().basic) {
			try {
					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yawForRot, pitchForRot, mc.player.onGround));

				BlockPos playerBlock;
				if (BlockUtils.isScaffoldPos((playerBlock = PlayerUtils.EntityPosToFloorBlockPos(mc.player)).add(0, -1, 0))) {
					if (BlockUtil.isValidBlock(playerBlock.add(0, -2, 0))) {
						this.place(playerBlock.add(0, -1, 0), EnumFacing.UP);
					} else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
						this.place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
					} else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 0))) {
						this.place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
					} else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, -1))) {
						this.place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
					} else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
						this.place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
					} else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
						if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
							this.place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
						}
						this.place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
					} else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 1))) {
						if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
							this.place(playerBlock.add(0, -1, 1), EnumFacing.WEST);
						}
						this.place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
					} else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
						if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
							this.place(playerBlock.add(0, -1, 1), EnumFacing.SOUTH);
						}
						this.place(playerBlock.add(1, -1, 1), EnumFacing.WEST);
					} else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
						if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
							this.place(playerBlock.add(0, -1, 1), EnumFacing.EAST);
						}
						this.place(playerBlock.add(1, -1, 1), EnumFacing.NORTH);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (mode.getSelected().legit) {

				mc.player.rotationYaw = (float) (startYaw - 180);
				mc.player.rotationPitch = 80;

			if (mc.player.onGround && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock().equals(Blocks.AIR)) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, true);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, false);
			} else {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindBack, true);
			}
		} else if (mode.getSelected().fallen) {
			if (mc.player.getHeldItemMainhand().getItem() != Items.AIR) {
				BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
				if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
					mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, mc.player.getHorizontalFacing().getOpposite(), new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}

					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw + 120, 90, mc.player.onGround));

			}
		} else if (mode.getSelected().fill) {
			if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemAir)) {
				for (int x = -3; x < 3; x++) {
					for (int z = -3; z < 3; z++) {
						BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY - mc.player.fallDistance, mc.player.posZ + z);
						if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
							mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, EnumFacing.DOWN, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
							float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));

								mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));

						}
					}
				}
			}
		}
	}

	public static double random(double min, double max) {
		return (min + (Math.random() * (max - min)));
	}

	public void place(BlockPos posI, EnumFacing face) {
		if (mode.getSelected().normal || mode.getSelected().basic) {
			try {
				Block block;
				BlockPos pos = posI;
				if (face == EnumFacing.UP) {
					pos = pos.add(0, -1, 0);
				} else if (face == EnumFacing.NORTH) {
					pos = pos.add(0, 0, 1);
				} else if (face == EnumFacing.SOUTH) {
					pos = pos.add(0, 0, -1);
				} else if (face == EnumFacing.EAST) {
					pos = pos.add(-1, 0, 0);
				} else if (face == EnumFacing.WEST) {
					pos = pos.add(1, 0, 0);
				}
				int oldSlot = Scaffold.mc.player.inventory.currentItem;
				int newSlot = -1;
				for (int i = 0; i < 9; ++i) {
					ItemStack stack = Scaffold.mc.player.inventory.getStackInSlot(i);
					if (InventoryUtil.isItemStackNull(stack) || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem((Item) stack.getItem()).getDefaultState().isFullBlock())
						continue;
					newSlot = i;
					break;
				}
				if (newSlot == -1) {
					return;
				}
				if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
					Scaffold.mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(newSlot));
					Scaffold.mc.player.inventory.currentItem = newSlot;
					Scaffold.mc.playerController.updateController();
				}
				if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.player.jump();
					if (mc.player.ticksExisted % 18 == 0) {
						mc.player.motionY = -0.25;
					}
				}
				if (mode.getSelected().normal) {
					float[] angle = MathUtils.calcAngle(Scaffold.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5f), (double) ((float) pos.getY() - 0.5f), (double) ((float) pos.getZ() + 0.5f)));
						yawForRot = angle[0];
						pitchForRot = (float) MathHelper.normalizeAngle((int) ((int) angle[1]), (int) 360);
				} else if (mode.getSelected().basic) {
						yawForRot = mc.player.rotationYaw - 180;
						pitchForRot = 90;
				}
				Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
				Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
				Scaffold.mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(oldSlot));
				Scaffold.mc.player.inventory.currentItem = oldSlot;
				Scaffold.mc.playerController.updateController();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}