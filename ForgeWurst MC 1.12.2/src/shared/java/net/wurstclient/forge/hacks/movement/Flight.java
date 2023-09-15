package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.MathUtils;

public final class Flight extends Hack
{

	private final SliderSetting upSpeed =
			new SliderSetting("Up-Speed", 1, 0.005, 10, 0.05, ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("Base-Speed", 1, 0.005, 10, 0.05, ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("Down-Speed", 1, 0.005, 10, 0.05, ValueDisplay.DECIMAL);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.VANILLA);

	private final SliderSetting ncpStength =
			new SliderSetting("NCP-Strength", "Strength = How many times we send a packet at once \n" +
					"To high may kick you or in result may actually perform worse.", 1, 1, 20, 1, ValueDisplay.DECIMAL);

	public Flight()
	{
		super("Flight",
				"Allows you to fly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(ncpStength);
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected() == Mode.NCP) {
			if (mc.player.ticksExisted > 20) {
				if (!mc.player.onGround) {
					if (mc.gameSettings.keyBindJump.isKeyDown()) {
						mc.player.setVelocity(0, +upSpeed.getValueF(), 0);
					} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.player.setVelocity(0, -downSpeed.getValueF(), 0);
					} else {
						mc.player.motionY = 0;
					}

					if (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
						mc.player.setVelocity(0, 0, 0);
					}

					if (mc.player.ticksExisted % 2 == 0) {
						mc.player.fallDistance = 50000 + Math.round(Math.random() * 50000);
					} else {
						mc.player.fallDistance = 50000 - Math.round(Math.random() * 50000);
					}

					if (mc.player.motionX > 0.26 || mc.player.motionX < -0.26 ||
							mc.player.motionZ > 0.26 || mc.player.motionZ < -0.26) {
						if (mc.player.motionX > 0) {
							mc.player.motionX = mc.player.motionX - 0.05;
						} else if (mc.player.motionX < 0) {
							mc.player.motionX = mc.player.motionX + 0.05;
						}
						if (mc.player.motionZ > 0) {
							mc.player.motionZ = mc.player.motionZ - 0.05;
						} else if (mc.player.motionZ < 0) {
							mc.player.motionZ = mc.player.motionZ + 0.05;
						}
					}

					for (int a = 0; a < ncpStength.getValueI(); a++) {
						mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
						if (mc.player.ticksExisted % 2 == 0) {
							mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY + 50000 + Math.round(Math.random() * 50000), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
						} else {
							mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY - 50000 - Math.round(Math.random() * 50000), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
						}
					}
				} else {
					mc.player.jump();
				}
			}
		} else if (mode.getSelected() == Mode.GHOSTLY) {
			mc.player.onGround = true;
			mc.player.isAirBorne = false;
			mc.player.fallDistance = 0;
			mc.player.collidedHorizontally = true;
			mc.player.collidedVertically = false;
			if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.player.motionY = 0;
				MathUtils.setSpeed(baseSpeed.getValueF());
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY + 0.02, mc.player.lastTickPosZ);
				} else {
					mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY - 0.02, mc.player.lastTickPosZ);
				}
			} else if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY = upSpeed.getValue();
			} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.player.motionY = -downSpeed.getValue();
			}
		} else if (mode.getSelected() == Mode.VANILLA) {
			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = baseSpeed.getValueF();

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY = +upSpeed.getValue();
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY = -downSpeed.getValue();
		}
	}

	@SubscribeEvent
	public void onPackets(WPacketInputEvent event) {
		if (mc.player.ticksExisted > 20) {
			if (!mc.player.onGround) {
				if (mode.getSelected() == Mode.NCP) {
					if (event.getPacket() instanceof SPacketPlayerPosLook) {
						SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
						for (int x = 0; x < ncpStength.getValueI(); x++) {
							mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
							mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), false));
						}
						mc.player.setPosition(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ());
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private enum Mode {
		GHOSTLY("Ghostly"),
		NCP("NCP"),
		VANILLA("Vanilla");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
}