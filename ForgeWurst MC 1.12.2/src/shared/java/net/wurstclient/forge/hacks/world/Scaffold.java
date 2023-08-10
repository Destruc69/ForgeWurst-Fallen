package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
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

	private final EnumSetting<Sneak> sneak =
			new EnumSetting<>("SneakType", Sneak.values(), Sneak.PACKET);

	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed", 0.9, 0.1, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	public Scaffold() {
		super("Scaffold", "Place blocks underneath you automatically.");
		setCategory(Category.WORLD);
		addSetting(sneak);
		addSetting(timerSpeed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		a = true;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50);
	}

	private boolean a;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.swingProgress > 0) {
			if (a) {
				setTickLength(50 / timerSpeed.getValueF());
				a = false;
			}
			if (sneak.getSelected() == Sneak.PACKET) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
			} else if (sneak.getSelected() == Sneak.LEGIT) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, true);
			}
		} else {
			if (sneak.getSelected() == Sneak.LEGIT) {
				KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
			} else if (sneak.getSelected() == Sneak.PACKET) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
			}
			if (!a) {
				setTickLength(50);
				a = true;
			}
		}
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
		double offsetX = 0.25D;
		double offsetY = 0.25D;
		double offsetZ = 0.25D;

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

	private enum Sneak {
		PACKET("Packet"),
		LEGIT("Legit"),
		OFF("Off");

		private final String name;

		private Sneak(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
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