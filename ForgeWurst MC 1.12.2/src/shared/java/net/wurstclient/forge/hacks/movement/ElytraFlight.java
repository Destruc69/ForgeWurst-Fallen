/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import com.sun.media.jfxmedia.events.PlayerStateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public final class ElytraFlight extends Hack {

	public static double startYNCP = 0;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", "Modes for ElytraFlight", Mode.values(), Mode.BOOST);

	private final SliderSetting upSpeed =
			new SliderSetting("UpSpeed", "Speed for going Up", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting baseSpeed =
			new SliderSetting("BaseSpeed", "Speed for going forwards, left, right and back", 0.4, 0, 10, 0.00005, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting downSpeed =
			new SliderSetting("DownSpeed", "Speed for going down", 0.4, 0, 2, 0.000005, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting takeoff =
			new CheckboxSetting("AutoTakeOff", "Sends packet to start elytra flying",
					false);

	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed", "Set game tick speed", 0.4, 0.1, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Helps bypass",
					false);

	private final CheckboxSetting fortyPitch =
			new CheckboxSetting("FortyPitch", "Maintain a 0 to 40 to -40",
					false);

	public ElytraFlight() {
		super("ElytraFlight", "Fly with elytras.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(upSpeed);
		addSetting(baseSpeed);
		addSetting(downSpeed);
		addSetting(takeoff);
		addSetting(timerSpeed);
		addSetting(bypass);
		addSetting(fortyPitch);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		startYNCP = mc.player.posY;
		jumpY = mc.player.posY;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		jumpY = 0;
	}

	@SubscribeEvent
	public void update(WUpdateEvent event) {
		if (!bypass.isChecked()) {
			if (baseSpeed.getValueF() == 0) {
				baseSpeed.setValue(Integer.MIN_VALUE);
			}
			if (downSpeed.getValueF() == 0) {
				downSpeed.setValue(Integer.MIN_VALUE);
			}
			if (upSpeed.getValueF() == 0) {
				upSpeed.setValue(Integer.MIN_VALUE);
			}
			if (mc.player.isElytraFlying()) {
				setTickLength(50 / timerSpeed.getValueF());
				if (mode.getSelected().boostnoy || mode.getSelected().boost) {
					boostElytraFlight();
					startYNCP = mc.player.posY;
				} else if (mode.getSelected().control) {
					controlElytraFlight();
					startYNCP = mc.player.posY;
				} else if (mode.getSelected().ncp) {

				} else if (mode.getSelected().dev) {
					devElytraFlight();
				} else if (mode.getSelected().jump) {
					jumpElytraFlight();
				} else if (mode.getSelected().firework) {
					fireWorkElytraFlight();
				}
			} else {
				setTickLength(50);
				if (takeoff.isChecked()) {
					if (mc.player.ticksExisted % 5 == 0) {
						mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
					}
				}
			}
		} else if (bypass.isChecked()) {
			if (mc.player.ticksExisted % 20 == 0) {
				if (baseSpeed.getValueF() == 0) {
					baseSpeed.setValue(Integer.MIN_VALUE);
				}
				if (downSpeed.getValueF() == 0) {
					downSpeed.setValue(Integer.MIN_VALUE);
				}
				if (upSpeed.getValueF() == 0) {
					upSpeed.setValue(Integer.MIN_VALUE);
				}
				if (mc.player.isElytraFlying()) {
					setTickLength(50 / timerSpeed.getValueF());
					if (mode.getSelected().boostnoy || mode.getSelected().boost) {
						boostElytraFlight();
						startYNCP = mc.player.posY;
					} else if (mode.getSelected().control) {
						controlElytraFlight();
						startYNCP = mc.player.posY;
					} else if (mode.getSelected().ncp) {

					} else if (mode.getSelected().dev) {
						devElytraFlight();
					} else if (mode.getSelected().jump) {
						jumpElytraFlight();
					} else if (mode.getSelected().firework) {
						fireWorkElytraFlight();
					}
				} else {
					setTickLength(50);
					if (takeoff.isChecked()) {
						if (mc.player.ticksExisted % 5 == 0) {
							mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
		if (fortyPitch.isChecked()) {
			if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				event.setPitch(-40);
			}
			if (mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
				event.setPitch(40);
			}
			if (!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
				event.setPitch(0);
			}
		}
	}

	public static double jumpY = 0;
	public void jumpElytraFlight() {
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			jumpY = jumpY + 1;
		}
		if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			jumpY = jumpY - 1;
		}
		if (mc.player.posY <= jumpY) {
			mc.player.jump();
		}
	}

	public void fireWorkElytraFlight() {
		if (mc.player.getHeldItemMainhand().getItem().equals(Items.FIREWORKS)) {
			if (mc.player.motionX < 0.5 && mc.player.motionX > -0.5 && mc.player.motionZ < 0.5 && mc.player.motionZ > -0.5) {
				if (mc.player.ticksExisted % 20 == 0) {
					mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);
				}
			}
		} else {
			setEnabled(false);
			try {
				ChatUtils.error("[EF] You need to be holding a firework to work for this mode!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void devElytraFlight() {
		double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());

		mc.player.motionX = mc.player.motionX + dir[0];
		mc.player.motionZ = mc.player.motionZ + dir[1];

		mc.player.rotateElytraX = 0;
		mc.player.rotateElytraY = 0;
		mc.player.rotateElytraZ = 0;
		mc.player.limbSwing = 0;
		mc.player.limbSwingAmount = 0;
		mc.player.prevLimbSwingAmount = 0;
	}

	@SubscribeEvent
	public void onPacketNCP(WPacketInputEvent event) {
		if (mc.player.isElytraFlying()) {
			try {
				double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
				double x = mc.player.posX + mc.player.motionX + dir[0];
				double y = mc.player.posY + mc.player.motionY + 2345;
				double z = mc.player.posZ + mc.player.motionZ + dir[1];
				float pitch = 0;
				if (mode.getSelected().ncp) {
					if (event.getPacket() instanceof CPacketPlayer.Position) {
						event.setPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
					}
					if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
						event.setPacket(new CPacketPlayer.PositionRotation(x, y, z, mc.player.rotationYaw, pitch, mc.player.onGround));
					}
					if (!Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_D) && !Keyboard.isKeyDown(Keyboard.KEY_S) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
						if (event.getPacket() instanceof CPacketPlayer.Rotation) {
							event.setCanceled(true);
						}
					} else {
						if (event.getPacket() instanceof CPacketPlayer.Rotation) {
							event.setPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, pitch, mc.player.onGround));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (mode.getSelected().jump) {
			FallenRenderUtils.renderPosFilled(new BlockPos(mc.player.posX, jumpY - 1, mc.player.posZ), event.getPartialTicks(), 1, 1, 0, 1);
		}
	}

	@SubscribeEvent
	public void onPacketOutNCP(WPacketOutputEvent event) {
		if (mc.player.isElytraFlying()) {
			if (mode.getSelected().ncp) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
					mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
					mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ(), sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), mc.player.onGround));
					mc.player.setPosition(sPacketPlayerPosLook.getX(), sPacketPlayerPosLook.getY(), sPacketPlayerPosLook.getZ());
					event.setCanceled(true);
				}
			}
		}
	}
	@SubscribeEvent
	public void onUpdateNCP(WUpdateEvent event) {
		if (mc.player.isElytraFlying()) {
			if (mode.getSelected().ncp) {
				//All movement should be suspended except x and z
				mc.player.motionY = 0;
				mc.player.limbSwingAmount = 0;
				mc.player.limbSwing = 0;
				mc.player.prevLimbSwingAmount = 0;
				mc.player.rotateElytraX = 0;
				mc.player.rotateElytraY = 0;
				mc.player.rotateElytraZ = 0;
				double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
				mc.player.setPosition(mc.player.posX + dir[0], startYNCP, mc.player.posZ + dir[1]);
			}
		}
	}

	public void boostElytraFlight() {
		float yaw = Minecraft.getMinecraft().player.rotationYaw;
		float pitch = Minecraft.getMinecraft().player.rotationPitch;
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			Minecraft.getMinecraft().player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			Minecraft.getMinecraft().player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * baseSpeed.getValueF();
			if (!mode.getSelected().boostnoy) {
				Minecraft.getMinecraft().player.motionY += Math.sin(Math.toRadians(pitch)) * upSpeed.getValueF();
			}
		}
		if (!mode.getSelected().boostnoy) {
			if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
				Minecraft.getMinecraft().player.motionY += upSpeed.getValueF();
			if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
				Minecraft.getMinecraft().player.motionY -= downSpeed.getValueF();
		}
	}

	public void controlElytraFlight() {
		boolean keysActive = mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown();
		boolean jumpActive = mc.gameSettings.keyBindJump.isKeyDown();
		boolean sneakActive = mc.gameSettings.keyBindSneak.isKeyDown();
		if (keysActive) {
			double[] dir = MathUtils.directionSpeed(baseSpeed.getValueF());
			mc.player.motionX = dir[0];
			mc.player.motionZ = dir[1];
			if (!jumpActive && !sneakActive) {
				mc.player.motionY = 0;
			}
		}
		if (!jumpActive && !sneakActive && !keysActive) {
			mc.player.motionY = 0;
			mc.player.motionX = 0;
			mc.player.motionZ = 0;
		}
		if (jumpActive) {
			mc.player.motionY += upSpeed.getValueF();
		}
		if (sneakActive) {
			mc.player.motionY -= downSpeed.getValueF();
		}
	}

	private enum Mode {
		BOOST("Boost", true, false, false, false, false, false, false),
		CONTROL("Control", false, true, false, false, false, false, false),
		BOOSTNOY("Boost-NO-Y", false, false, true, false, false, false, false),
		NCP("NCP", false, false, false, true, false, false, false),
		DEV("Dev", false, false, false, false, true, false, false),
		JUMP("Jump", false, false, false, false, false, true, false),
		FIREWORK("Firework", false, false, false, false, false, false, true);

		private final String name;
		private final boolean boost;
		private final boolean control;
		private final boolean boostnoy;
		private final boolean ncp;
		private final boolean dev;
		private final boolean jump;
		private final boolean firework;

		private Mode(String name, boolean boost, boolean control, boolean boostnoy, boolean ncp, boolean dev, boolean jump, boolean firework) {
			this.name = name;
			this.boost = boost;
			this.control = control;
			this.boostnoy = boostnoy;
			this.ncp = ncp;
			this.dev = dev;
			this.jump = jump;
			this.firework = firework;
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