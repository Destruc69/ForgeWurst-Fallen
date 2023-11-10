package net.wurstclient.forge.hacks.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.*;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WVec3d;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class FreeCam extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private final SliderSetting speed =
			new SliderSetting("Speed", 1, 0.05, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private EntityPlayer entityOtherPlayerMP;

	private Vec3d playerPosSave;

	public FreeCam() {
		super("FreeCam", "Go outside of your body.");
		setCategory(Category.RENDER);
		addSetting(speed);
		addSetting(mode);
	}

	@Override
	public void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		if (mode.getSelected() == Mode.CAMERA) {
			entityOtherPlayerMP = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
			//mc.world.addEntityToWorld(-100, entityOtherPlayerMP);
			mc.world.spawnEntity(entityOtherPlayerMP);
			entityOtherPlayerMP.copyLocationAndAnglesFrom(mc.player);
			mc.setRenderViewEntity(entityOtherPlayerMP);
		} else {
			playerPosSave = new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
		}
	}

	@Override
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);

		if (mode.getSelected() == Mode.CAMERA) {
			mc.setRenderViewEntity(mc.player);
			mc.world.removeEntity(entityOtherPlayerMP);
		} else {
			mc.player.setPosition(playerPosSave.x, playerPosSave.y, playerPosSave.z);
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected() == Mode.CAMERA) {
			if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() ||
					Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ||
					Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ||
					Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
				entityOtherPlayerMP.motionX = -MathHelper.sin(getDirection()) * speed.getValue();
				entityOtherPlayerMP.motionZ = MathHelper.cos(getDirection()) * speed.getValue();
			} else {
				entityOtherPlayerMP.motionX = 0;
				entityOtherPlayerMP.motionZ = 0;
			}
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				entityOtherPlayerMP.motionY = speed.getValue();
			} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				entityOtherPlayerMP.motionY = -speed.getValue();
			} else {
				entityOtherPlayerMP.motionY = 0;
			}

			entityOtherPlayerMP.noClip = true;

			entityOtherPlayerMP.rotationYaw = mc.player.rotationYaw;
			entityOtherPlayerMP.rotationPitch = mc.player.rotationPitch;

			entityOtherPlayerMP.move(MoverType.SELF, entityOtherPlayerMP.motionX, entityOtherPlayerMP.motionY, entityOtherPlayerMP.motionZ);
		} else {
			EntityPlayerSP player = event.getPlayer();

			player.onGround = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = speed.getValueF();

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValue();
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValue();
		}
	}

	private float getDirection() {
		float yaw = entityOtherPlayerMP.rotationYaw;
		final float forward = mc.gameSettings.keyBindForward.isKeyDown() ? 1 : (mc.gameSettings.keyBindBack.isKeyDown() ? -1 : 0);
		final float strafe = mc.gameSettings.keyBindLeft.isKeyDown() ? 1 : (mc.gameSettings.keyBindRight.isKeyDown() ? -1 : 0);;
		yaw += ((forward < 0.0f) ? 180 : 0);
		int i = (forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45);
		if (strafe < 0.0f) {
			yaw += i;
		}
		if (strafe > 0.0f) {
			yaw -= i;
		}
		return yaw * 0.017453292f;
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		event.setCanceled(true);
	}

	private enum Mode {
		CAMERA("Camera"),
		NORMAL("Normal");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	@SubscribeEvent
	public void onPlayerMove(WPlayerMoveEvent event) {
		if (mode.getSelected() == Mode.NORMAL) {
			event.getPlayer().noClip = true;
		}
	}

	@SubscribeEvent
	public void onIsNormalCube(WIsNormalCubeEvent event) {
		if (mode.getSelected() == Mode.NORMAL) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onSetOpaqueCube(WSetOpaqueCubeEvent event) {
		if (mode.getSelected() == Mode.NORMAL) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (mode.getSelected() == Mode.NORMAL) {
			try {
				if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
					event.setCanceled(true);
				}
			} catch (Exception ignored) {
			}
		}
	}

	private float[] getNeededRotations(Vec3d vec)
	{
		Vec3d eyesPos = new Vec3d(entityOtherPlayerMP.lastTickPosX, entityOtherPlayerMP.lastTickPosY + entityOtherPlayerMP.eyeHeight, entityOtherPlayerMP.lastTickPosZ);

		double diffX = WVec3d.getX(vec) - WVec3d.getX(eyesPos);
		double diffY = WVec3d.getY(vec) - WVec3d.getY(eyesPos);
		double diffZ = WVec3d.getZ(vec) - WVec3d.getZ(eyesPos);

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float)-Math.toDegrees(Math.atan2(diffY, diffXZ));

		return new float[]{MathHelper.wrapDegrees(yaw),
				MathHelper.wrapDegrees(pitch)};
	}
}