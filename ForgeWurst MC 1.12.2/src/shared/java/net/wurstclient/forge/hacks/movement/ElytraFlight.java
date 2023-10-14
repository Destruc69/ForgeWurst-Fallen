package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;

import java.lang.reflect.Field;

public final class ElytraFlight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.CONTROL);

	private final SliderSetting upSpeed =
			new SliderSetting("Up-Speed", 1, 0, 3, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("Base-Speed", 1, 0, 3, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("Down-Speed", 1, 0, 3, 0.01, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting autoTakeOff =
			new CheckboxSetting("AutoTakeOff", "Takes off automatically.",
					false);

	private final CheckboxSetting shouldGlide =
			new CheckboxSetting("ShouldGlide", "Should we glide?.",
					false);

	private final SliderSetting glide =
			new SliderSetting("Glide", "2 == half normal glide speed", 1, 0.1, 50, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting shouldLockPitch =
			new CheckboxSetting("ShouldLockPitch", "Should we lock pitch?.",
					false);

	private final SliderSetting lockPitch =
			new SliderSetting("LockPitch", 4, -10, 10, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private int jumpTimer;

	public ElytraFlight()
	{
		super("ElytraFlight", "Fly with an elytra.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(autoTakeOff);
		addSetting(shouldGlide);
		addSetting(glide);
		addSetting(shouldLockPitch);
		addSetting(lockPitch);
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

	private boolean a;

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.isElytraFlying()) {
			if (shouldLockPitch.isChecked()) {
				mc.player.rotationPitch = lockPitch.getValueF();
			}
			if (shouldGlide.isChecked()) {
				if (glide.getValue() > 0) {
					if (mc.player.motionY < 0) {
						mc.player.motionY = -mc.player.motionY / -glide.getValueF();
					}
				}
			}
			if (mode.getSelected() == Mode.BOOST ||
					mode.getSelected() == Mode.BOOSTPLUS) {
				boostEF();
			} else if (mode.getSelected() == Mode.CONTROL) {
				controlEF();
			} else if (mode.getSelected() == Mode.WUYRST7) {
				wurst7EF();
			}

			if (!a) {
				setTickLength(50);
			}
		} else if (!(mode.getSelected() == Mode.WUYRST7)) {
			if (autoTakeOff.isChecked()) {
				if (mc.player.onGround) {
					//mc.player.jump();
					//mc.player.motionX = 0;
					//mc.player.motionZ = 0;
					a = true;
				} else if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.5, mc.player.posZ)).getBlock().equals(Blocks.AIR)) {
					if (mc.player.ticksExisted % 2 == 0) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
					}
					if (a) {
						setTickLength(50 / 0.05f);
						a = false;
					}
				}
			}
		}

		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityFireworkRocket) {
				if (entity.ticksExisted > 0) {
					mc.world.removeEntity(entity);
				}
			}
		}

		if (mode.getSelected() == Mode.PACKET) {
			packetEF();
		}
	}

	private void packetEF() {
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY = upSpeed.getValueF();
		} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = -downSpeed.getValue();
		} else {
			mc.player.motionY = MathUtils.calculateYMotionForElytraFlight(mc.player.rotationPitch, mc.player.motionY);
		}

		if (isKeyInputs()) {
			MathUtils.setSpeed(baseSpeed.getValueF());
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}

		mc.player.connection.sendPacket(new CPacketPlayer(true));
		mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
	}

	private void wurst7EF() {
		if(jumpTimer > 0)
			jumpTimer--;

		ItemStack chest = mc.player.inventory.armorItemInSlot(2);
		if(chest.getItem() != Items.ELYTRA)
			return;

		if(mc.player.isElytraFlying())
		{
			controlSpeed();
			controlHeight();
			return;
		}

		if(ItemElytra.isUsable(chest) && mc.gameSettings.keyBindJump.isKeyDown() && autoTakeOff.isChecked())
			doInstantFly();
	}

	private void sendStartStopPacket()
	{
		CPacketEntityAction packet = new CPacketEntityAction(mc.player,
				CPacketEntityAction.Action.START_FALL_FLYING);
		mc.player.connection.sendPacket(packet);
	}

	private void controlHeight()
	{

		Vec3d v = new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);

		if(mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.setVelocity(v.x, v.y + 0.08, v.z);
		} else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.setVelocity(v.x, v.y - 0.04, v.z);
		}
	}

	private void controlSpeed()
	{
		float yaw = (float)Math.toRadians(mc.player.rotationYaw);
		Vec3d forward = new Vec3d(-MathHelper.sin(yaw) * 0.05, 0,
				MathHelper.cos(yaw) * 0.05);

		Vec3d v = new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);

		if(mc.gameSettings.keyBindForward.isKeyDown())
			mc.player.setVelocity(v.add(forward).x, v.add(forward).y, v.add(forward).z);
		else if(mc.gameSettings.keyBindBack.isKeyDown())
			mc.player.setVelocity(v.subtract(forward).x, v.subtract(forward).y, v.subtract(forward).z);
	}

	private void doInstantFly()
	{
		if(jumpTimer <= 0)
		{
			jumpTimer = 20;
			mc.player.setJumping(false);
			mc.player.setSprinting(true);
			mc.player.jump();
		}

		sendStartStopPacket();
	}

	private void boostEF() {
		if (mode.getSelected() == Mode.BOOST) {
			float yaw = Minecraft.getMinecraft().player.rotationYaw;
			float pitch = Minecraft.getMinecraft().player.rotationPitch;
			if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
				Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
				Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			}
			if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
			if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
				Minecraft.getMinecraft().player.motionY -= Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
		} else if (mode.getSelected() == Mode.BOOSTPLUS) {
			double y = 0;
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				y = upSpeed.getValueF();
			} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				y = -downSpeed.getValueF();
			}

			double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
			Vec3d vec3d = mc.player.getLookVec();
			float f = mc.player.rotationPitch * 0.017453292F;
			double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
			double d8 = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
			double d1 = vec3d.lengthVector();
			float f4 = MathHelper.cos(f);
			f4 = (float) (f4 * f4 * Math.min(1.0D, d1 / 0.4D));

			mc.player.motionY += -0.08D + f4 * 0.06D + y;

			if (mc.player.motionY < 0.0D && d6 > 0.0D) {
				double d2 = mc.player.motionY * -0.1D * f4 + y;
				mc.player.motionY += d2;
				mc.player.motionX += (vec3d.x * d2 / d6 + dir[0]) * 0.5D;
				mc.player.motionZ += (vec3d.z * d2 / d6 + dir[1]) * 0.5D;
			}

			if (f < 0.0F) {
				double d10 = d8 * (-MathHelper.sin(f)) * 0.04D;
				mc.player.motionY += d10 * 3.2D + y;
				mc.player.motionX -= (vec3d.x * d10 / d6 + dir[0]) * 0.5D;
				mc.player.motionZ -= (vec3d.z * d10 / d6 + dir[1]) * 0.5D;
			}

			if (d6 > 0.0D) {
				mc.player.motionX += ((vec3d.x / d6 * d8 - mc.player.motionX) + dir[0]) * 0.05D;
				mc.player.motionZ += ((vec3d.z / d6 * d8 - mc.player.motionZ) + dir[1]) * 0.05D;
			}

			mc.player.motionX *= 0.9900000095367432D;
			mc.player.motionY *= 0.9800000190734863D;
			mc.player.motionZ *= 0.9900000095367432D;

			// mc.player.move(MoverType.SELF, mc.player.motionX, mc.player.motionY, mc.player.motionZ);
		}
	}

	private void controlEF() {
		if (isKeyInputs()) {
			setSpeed(baseSpeed.getValueF());
		} else {
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.player.motionY = upSpeed.getValue();
		} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.player.motionY = -downSpeed.getValue();
		} else {
			mc.player.motionY = MathUtils.calculateYMotionForElytraFlight(mc.player.rotationPitch, mc.player.motionY);
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketOutputEvent event) {
		if (mc.player.isElytraFlying()) {
			if (mode.getSelected() == Mode.CONTROL && !isKeyInputs() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				if (event.getPacket() instanceof CPacketPlayer ||
						event.getPacket() instanceof CPacketPlayer.Rotation ||
						event.getPacket() instanceof CPacketPlayer.PositionRotation ||
						event.getPacket() instanceof CPacketPlayer.Position) {
					event.setCanceled(true);
				}
			}
		}
	}

	private enum Mode {
		CONTROL("Control"),
		BOOST("Boost"),
		BOOSTPLUS("BoostPlus"),
		WUYRST7("Wurst7"),
		PACKET("Packet");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private void setSpeed(final double speed) {
		Minecraft.getMinecraft().player.motionX = -MathHelper.sin(getDirection()) * speed;
		Minecraft.getMinecraft().player.motionZ = MathHelper.cos(getDirection()) * speed;
	}

	private boolean isKeyInputs() {
		return Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown() ||
				Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown();
	}

	private float getDirection() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		final float forward = Minecraft.getMinecraft().player.moveForward;
		final float strafe = Minecraft.getMinecraft().player.moveStrafing;
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