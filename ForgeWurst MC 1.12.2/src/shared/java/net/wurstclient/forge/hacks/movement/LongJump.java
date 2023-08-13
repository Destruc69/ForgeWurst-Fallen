/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;

public final class LongJump extends Hack {
	private final Minecraft mc = Minecraft.getMinecraft();
	
	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.OLDAAC);

	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass", "Helps bypass, will be slower.",
					false);
	
	private int groundTick;
	private boolean jump;
	private int stage;
	private float air;
	private int airTicks;

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
		groundTick = 0;
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
			mc.player.prevPosY = 0;
			float x2 = (float) (1f + 0.2873D * 0.45f);
			if ((mc.player.moveForward != 0 || mc.player.moveStrafing != 0) && mc.player.onGround) {
				if (groundTick > 0) {
					groundTick = 0;
					return;
				}
				stage = 1;
				groundTick++;

				mc.player.jump();

			}
			if (mc.player.onGround) {
				air = 0;
			} else {
				if (mc.player.collidedVertically)
					stage = 0;
				if (stage > 0 && stage <= 3) {
					//if(mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(-0.3, -2, -0.3).expand(0.3, 0, 0.3)).isEmpty()){
					mc.player.onGround = true;
					//  }

					//mc.player.isCollidedVertically = false;
				}
				double speed = (0.75f + 0.2873D * 0.2f) - air / 25;
				if (speed < 0.2873D) { // + (0.025*MoveUtils.getSpeedEffect())
					// speed = 0.2873D;
				}

				speed = (0.8f + 0.2873D * 0.2f) - air / 25;
				if (speed < 0.2873D) { // + (0.025*MoveUtils.getSpeedEffect())
					speed = 0.2873D;
				}

				mc.player.jumpMovementFactor = 0;
				if (stage < 4)
					speed = 0.2873D;
				MathUtils.setSpeed(speed);

				mc.player.motionY = getMotion(stage);

				if (stage > 0) {
					stage++;
				}
				air += x2;
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

	private void toFwd(double speed) {
		float yaw = mc.player.rotationYaw * 0.017453292f;
		mc.player.motionX -= (double) MathHelper.sin(yaw) * speed;
		mc.player.motionZ += (double)MathHelper.cos(yaw) * speed;
	}

	public boolean MovementInput() {
		return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown();
	}

	@SubscribeEvent
	public void onPostMotion(WUpdateEvent event) {
		if (mc.player.onGround) {
			++this.groundTick;
			this.airTicks = 0;
		} else {
			++this.airTicks;
		}
	}

	public void updatePosition(double x, double y, double z) {
		mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, mc.player.onGround));
	}

	public void setSpeed(double speed) {
		mc.player.motionX = - Math.sin(getDirection()) * speed;
		mc.player.motionZ = Math.cos(getDirection()) * speed;
	}

	private float getDirection() {
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

	private double getMotion(int stage){
		boolean isMoving = (mc.player.moveStrafing != 0 || mc.player.moveForward != 0);
		double[] mot = {0.396,-0.122,-0.1,0.423, 0.35,0.28,0.217,0.15, 0.025,-0.00625,-0.038,-0.0693,-0.102,-0.13,
				-0.018,-0.1,-0.117,-0.14532,-0.1334, -0.1581, -0.183141, -0.170695, -0.195653, -0.221, -0.209, -0.233, -0.25767,
				-0.314917, -0.371019, -0.426};
		stage --;
		if(stage >= 0 && stage < mot.length){
			return mot[stage];
		}else{
			return mc.player.motionY;
		}
	}
}