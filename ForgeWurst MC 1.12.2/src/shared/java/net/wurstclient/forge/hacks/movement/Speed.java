/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.MathUtils;

import javax.swing.*;
import java.util.List;

public final class Speed extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.BHOP);

	private double speed;
	private int stage;

	private double lastDist;
	
	private boolean collided;
	private double stair;
	private double less;
	private boolean lessSlow;
	private Timer timer;

	private boolean firstJump;
	private int tick;

	public Speed() {
		super("Speed", "I Show Speed");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		mc.player.setSprinting(false);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		mc.player.setSprinting(true);
		if (mode.getSelected() == Mode.BHOP) {
			if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
				speed = 0.2873D;
			}
			if (stage == 1 && mc.player.collidedVertically && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				speed = 1.35 + 0.2873D - 0.01;
			}
			double lastDist = 0;
			if (stage == 2 && mc.player.collidedVertically && mc.player.onGround && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				mc.player.jump();
				speed *= 1.533D;
			} else if (stage == 3) {
				final double difference = 0.66 * (lastDist - 0.2873D);
				speed = lastDist - difference;
			} else {
				final List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
				if ((collidingList.size() > 0 || mc.player.collidedVertically) && stage > 0) {
					stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
				}
				speed = lastDist - lastDist / 159.0;
			}
			speed = Math.max(speed, 0.2873D);

			// Stage checks if you're greater than 0 as step sets you -6 stage to make sure the player won't flag.
			if (stage > 0) {
				// Set strafe motion.
				MathUtils.setSpeed(speed);
			}
			// If the player is moving, step the stage up.
			if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
				++stage;
			}
		} else if (mode.getSelected() == Mode.OLDHOP) {
			if ((mc.player.moveForward == 0.0F) && (mc.player.moveStrafing == 0.0F)) {
				speed = 0.2873D;
			}
			if ((stage == 1) && (mc.player.collidedVertically) && ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F))) {
				speed = (0.25D + 0.2873D - 0.01D);
			} else if ((stage == 2) && (mc.player.collidedVertically) && mc.player.onGround && ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F))) {
				mc.player.motionY = 0.4D;
				mc.player.jump();
				speed *= 2.149D;
			} else if (stage == 3) {
				double difference = 0.66D * (lastDist - 0.2873D);
				speed = (0.2873D - difference);
			} else {
				List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D));
				if ((collidingList.size() > 0) || (mc.player.collidedVertically)) {
					if (stage > 0) {
						if (1.35D * 0.2873D - 0.01D > speed) {
							stage = 0;
						} else {
							stage = (mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F) ? 1 : 0;
						}
					}
				}
				speed = (lastDist - lastDist / 159.0D);
			}
			speed = Math.max(speed, 0.2873D);
			if (stage > 0) {
				MathUtils.setSpeed(speed);
			}
			if ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F)) {
				stage += 1;
			}
		} else if (mode.getSelected() == Mode.NCPBASIC) {
			if (mc.gameSettings.keyBindForward.isKeyDown() ||
			mc.gameSettings.keyBindRight.isKeyDown() ||
			mc.gameSettings.keyBindBack.isKeyDown() ||
			mc.gameSettings.keyBindLeft.isKeyDown()) {
				if (mc.player.onGround) {
					mc.player.jump();
				} else {
					MathUtils.setSpeed(0.26);
				}
			}
		} else if (mode.getSelected() == Mode.AAC) {
			if (mc.gameSettings.keyBindForward.isKeyDown() ||
					mc.gameSettings.keyBindRight.isKeyDown() ||
					mc.gameSettings.keyBindBack.isKeyDown() ||
					mc.gameSettings.keyBindLeft.isKeyDown()) {
				if (mc.player.hurtTime < 4) {
					if (mc.player.onGround) {
						if (!firstJump) {
							firstJump = true;
						}
						mc.player.jump();
						mc.player.motionY = 0.407;
					} else {
						firstJump = false;
						mc.player.motionY -= 0.0149;
					}
					toFwd(firstJump ? 0.0000 : 0.0005);
				}
			} else {
				tick = 0;
				mc.player.motionX = mc.player.motionZ = 0.0;
			}
		} else if (mode.getSelected() == Mode.TUNNEL) {
			BlockPos blockPos = mc.player.getPosition().add(0, 2, 0);
			if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR)) {
				if (mc.player.motionX > 0 || mc.player.motionX < 0 || mc.player.motionZ > 0 || mc.player.motionZ < 0) {
					if (mc.player.onGround) {
						mc.player.jump();
					}
				}
			}
		}
	}

	private void toFwd(double speed) {
		float yaw = mc.player.rotationYaw * 0.017453292f;
		mc.player.motionX -= (double) MathHelper.sin(yaw) * speed;
		mc.player.motionZ += (double)MathHelper.cos(yaw) * speed;
	}

	private enum Mode {
		BHOP("BHop"),
		OLDHOP("OldHop"),
		NCPBASIC("NCPBasic"),
		AAC("AAC"),
		TUNNEL("Tunnel");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
}
