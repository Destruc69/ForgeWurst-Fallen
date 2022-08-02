package net.wurstclient.forge.hacks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
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
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;

import java.lang.reflect.Field;

public final class Scaffold extends Hack {
	Vec3d vec3d;

	private final SliderSetting rotStrength =
			new SliderSetting("RotationStrength", "How strong are the rotations?", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Scaffold() {
		super("Scaffold", "thank you phobos my beloved");
		setCategory(Category.WORLD);
		addSetting(rotStrength);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.isSwingInProgress) {
			setTickLength(50 / 0.5f);
			mc.player.setVelocity(mc.player.motionX / 2, mc.player.motionY, mc.player.motionZ / 2);
		} else {
			setTickLength(50);
		}
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
	}

	public static double random(double min, double max) {
		return (min + (Math.random() * (max - min)));
	}

	public void place(BlockPos posI, EnumFacing face) {
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
			Scaffold.mc.player.motionX *= 0.3;
			Scaffold.mc.player.motionZ *= 0.3;
			Scaffold.mc.player.jump();
			if (TimerUtils.hasReached(100)) {
				Scaffold.mc.player.motionY = -0.25;
				TimerUtils.reset();
			}
		}
		float[] angle = MathUtils.calcAngle(Scaffold.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5f), (double) ((float) pos.getY() - 0.5f), (double) ((float) pos.getZ() + 0.5f)));
		float yawForRot = angle[0];
		float pitchForRot = (float) MathHelper.normalizeAngle((int) ((int) angle[1]), (int) 360);
		for (int x = 0; x < rotStrength.getValueF(); x++) {
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yawForRot, pitchForRot, mc.player.onGround));
		}
		Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
		Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
		Scaffold.mc.player.connection.sendPacket((Packet) new CPacketHeldItemChange(oldSlot));
		Scaffold.mc.player.inventory.currentItem = oldSlot;
		Scaffold.mc.playerController.updateController();
	}
	private void setTickLength(float tickLength)
	{
		try
		{
			Field fTimer = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);

			if(WMinecraft.VERSION.equals("1.10.2"))
			{
				Field fTimerSpeed = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);

			}else
			{
				Field fTickLength = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(mc), tickLength);
			}

		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
