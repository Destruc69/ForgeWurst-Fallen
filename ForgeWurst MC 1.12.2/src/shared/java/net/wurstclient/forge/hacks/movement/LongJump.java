/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.NotiUtils;

public final class LongJump extends Hack {

	private boolean jump;
	private double lastHDistance;
	private double groundTicks;
	private boolean isSpeeding;
	private int airTicks;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.OLDAAC);

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Helps bypass, will be slower.",
					false);

	private enum Mode {
		OLDAAC("OldAAC", true, false, false, false, false),
		NCP("NCP", false, true, false, false, false),
		OTHERNCP("OtherNCP", false, false, true, false, false),
		MINESECURE("MineSecure", false, false, false, true, false),
		GUARDIAN("Guardian", false, false, false, false, true);


		private final String name;
		private final boolean oldaac;
		private final boolean ncp;
		private final boolean otherncp;
		private final boolean minesecure;
		private final boolean guardian;

		private Mode(String name, boolean oldacc, boolean ncp, boolean otherncp, boolean minesecure, boolean guardian) {
			this.name = name;
			this.oldaac = oldacc;
			this.ncp = ncp;
			this.otherncp = otherncp;
			this.minesecure = minesecure;
			this.guardian = guardian;

		}

		public String toString() {
			return name;
		}
	}

	public LongJump() {
		super("LongJump", "Jump far");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(bypass);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		mc.player.setVelocity(0, mc.player.motionY, 0);
		groundTicks = 0;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.player.setVelocity(0, mc.player.motionY, 0);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (bypass.isChecked()) {
			if (mc.player.isAirBorne) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.motionX = mc.player.motionX / 1.05;
					mc.player.motionZ = mc.player.motionZ / 1.05;
				}
			}
		}
		if (mode.getSelected().oldaac) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
			if (mc.player.onGround) {
				jump = true;
			}
			if (mc.player.onGround) {
				mc.player.motionY = 0.42;
				toFwd(2.3);
			} else if (jump) {
				mc.player.motionZ = 0.0;
				mc.player.motionX = 0.0;
				jump = false;
			}
		} else if (mode.getSelected().ncp) {
			if (MovementInput() && mc.player.fallDistance < 1.0f) {
				float direction = mc.player.rotationYaw;
				float x = (float) Math.cos((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
				float z = (float) Math.sin((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
				if (mc.player.collidedVertically && MovementInput() && mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.player.motionX = x * 0.29f;
					mc.player.motionZ = z * 0.29f;
				}
				if (mc.player.motionY == 0.33319999363422365 && MovementInput()) {
					mc.player.motionX = (double) x * 1.261;
					mc.player.motionZ = (double) z * 1.261;
				}
			}
		} else if (mode.getSelected().otherncp) {
			EntityPlayerSP player = mc.player;
			if (!MovementInput()) {
				return;
			}
			if (mc.player.onGround) {
				lastHDistance = 0;
			}
			float direction = mc.player.rotationYaw + (float) (mc.player.moveForward < 0.0f ? 180 : 0) + (mc.player.moveStrafing > 0.0f ? -90.0f * (mc.player.moveForward > 0.0f ? 0.5f : (mc.player.moveForward < 0.0f ? -0.5f : 1.0f)) : 0.0f) - (mc.player.moveStrafing < 0.0f ? -90.0f * (mc.player.moveForward > 0.0f ? 0.5f : (mc.player.moveForward < 0.0f ? -0.5f : 1.0f)) : 0.0f);
			float xDir = (float) Math.cos((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
			float zDir = (float) Math.sin((double) (direction + 90.0f) * 3.141592653589793 / 180.0);
			if (!mc.player.collidedVertically) {
				this.isSpeeding = true;
				this.groundTicks = 0;
				if (!mc.player.collidedVertically) {
					if (mc.player.motionY == -0.07190068807140403) {
						player.motionY *= 0.3499999940395355;
					} else if (mc.player.motionY == -0.10306193759436909) {
						player.motionY *= 0.550000011920929;
					} else if (mc.player.motionY == -0.13395038817442878) {
						player.motionY *= 0.6700000166893005;
					} else if (mc.player.motionY == -0.16635183030382) {
						player.motionY *= 0.6899999976158142;
					} else if (mc.player.motionY == -0.19088711097794803) {
						player.motionY *= 0.7099999785423279;
					} else if (mc.player.motionY == -0.21121925191528862) {
						player.motionY *= 0.20000000298023224;
					} else if (mc.player.motionY == -0.11979897632390576) {
						player.motionY *= 0.9300000071525574;
					} else if (mc.player.motionY == -0.18758479151225355) {
						player.motionY *= 0.7200000286102295;
					} else if (mc.player.motionY == -0.21075983825251726) {
						player.motionY *= 0.7599999904632568;
					}
					if (mc.player.motionY < -0.2 && mc.player.motionY > -0.24) {
						player.motionY *= 0.7;
					}
					if (mc.player.motionY < -0.25 && mc.player.motionY > -0.32) {
						player.motionY *= 0.8;
					}
					if (mc.player.motionY < -0.35 && mc.player.motionY > -0.8) {
						player.motionY *= 0.98;
					}
					if (mc.player.motionY < -0.8 && mc.player.motionY > -1.6) {
						player.motionY *= 0.99;
					}
				}
				double[] speedVals = new double[]{0.420606, 0.417924, 0.415258, 0.412609, 0.409977, 0.407361, 0.404761, 0.402178, 0.399611, 0.39706, 0.394525, 0.392, 0.3894, 0.38644, 0.383655, 0.381105, 0.37867, 0.37625, 0.37384, 0.37145, 0.369, 0.3666, 0.3642, 0.3618, 0.35945, 0.357, 0.354, 0.351, 0.348, 0.345, 0.342, 0.339, 0.336, 0.333, 0.33, 0.327, 0.324, 0.321, 0.318, 0.315, 0.312, 0.309, 0.307, 0.305, 0.303, 0.3, 0.297, 0.295, 0.293, 0.291, 0.289, 0.287, 0.285, 0.283, 0.281, 0.279, 0.277, 0.275, 0.273, 0.271, 0.269, 0.267, 0.265, 0.263, 0.261, 0.259, 0.257, 0.255, 0.253, 0.251, 0.249, 0.247, 0.245, 0.243, 0.241, 0.239, 0.237};
				if (mc.gameSettings.keyBindForward.isKeyDown()) {
					try {
						mc.player.motionX = (double) xDir * speedVals[airTicks - 1] * 3.0;
						mc.player.motionZ = (double) zDir * speedVals[airTicks - 1] * 3.0;
					} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
					}
				} else {
					mc.player.motionX = 0.0;
					mc.player.motionZ = 0.0;
				}
			} else {
				airTicks = 0;
				player.motionX /= 13.0;
				player.motionZ /= 13.0;
				if (this.groundTicks == 1) {
					updatePosition(mc.player.posX, mc.player.posY, mc.player.posZ);
					updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
					updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
					updatePosition(mc.player.posX + 0.0624, mc.player.posY, mc.player.posZ);
					updatePosition(mc.player.posX, mc.player.posY + 0.419, mc.player.posZ);
				} else if (this.groundTicks > 2) {
					this.groundTicks = 0;
					mc.player.motionX = (double) xDir * 0.3;
					mc.player.motionZ = (double) zDir * 0.3;
					mc.player.motionY = 0.42399999499320984;
				}
			}
		} else if (mode.getSelected().minesecure) {
			if (mc.player.onGround && MovementInput() && !mc.player.isInWater()) {
				mc.player.motionY = 0.54;
			} else if (MovementInput() && !mc.player.isInWater()) {
				setSpeed(3.0);
			}
			if (!MovementInput()) {
				mc.player.motionZ = 0.0;
				mc.player.motionX = 0.0;
			}
		} else if (mode.getSelected().guardian) {
			if (mc.gameSettings.keyBindForward.isKeyDown() && mc.player.onGround) {
				mc.player.motionY = 0.41764345;
				toFwd(0.4);
			}
			if (!MovementInput()) {
				mc.player.motionZ = 0.0;
				mc.player.motionX = 0.0;
			}
		}
	}

	private static void toFwd(double speed) {
		float yaw = mc.player.rotationYaw * 0.017453292f;
		mc.player.motionX -= (double) MathHelper.sin(yaw) * speed;
		mc.player.motionZ += (double)MathHelper.cos(yaw) * speed;
	}

	public static boolean MovementInput() {
		return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown();
	}

	@SubscribeEvent
	public void onPostMotion(WUpdateEvent event) {
		if (mc.player.onGround) {
			++this.groundTicks;
			this.airTicks = 0;
		} else {
			++this.airTicks;
		}
	}

	public void updatePosition(double x, double y, double z) {
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
	}

	public static void setSpeed(double speed) {
		mc.player.motionX = - Math.sin(getDirection()) * speed;
		mc.player.motionZ = Math.cos(getDirection()) * speed;
	}

	public static float getDirection() {
		float yaw = mc.player.rotationYaw;
		if (mc.player.moveForward < 0.0f) {
			yaw += 180.0f;
		}
		float forward = 1.0f;
		if (mc.player.moveForward < 0.0f) {
			forward = -0.5f;
		} else if (mc.player.moveForward > 0.0f) {
			forward = 0.5f;
		}
		if (mc.player.moveStrafing > 0.0f) {
			yaw -= 90.0f * forward;
		}
		if (mc.player.moveStrafing < 0.0f) {
			yaw += 90.0f * forward;
		}
		return yaw * 0.017453292f;
	}
}