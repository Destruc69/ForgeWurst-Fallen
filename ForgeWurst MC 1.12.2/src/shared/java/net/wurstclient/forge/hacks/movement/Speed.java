/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.MathUtils;

import java.util.List;
import java.util.Objects;

public final class Speed extends Hack {

	private double speed;
	private int stage;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.BHOP);
	private double lastDist;
	private boolean collided;
	private int stair;
	private double less;
	private boolean lessSlow;
	private int aacCount;
	private int count;
	private int air;

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
				speed = 0.26;
			}
			if (stage == 1 && mc.player.collidedVertically && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				speed = 1.35 + 0.26 - 0.01;
			}
			if (stage == 2 && mc.player.collidedVertically && mc.player.onGround && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
					mc.player.motionY = 0.41999998688698 + (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1;
				else
					mc.player.motionY = 0.41999998688698;
				mc.player.jump();
				speed *= 1.533D;
			} else if (stage == 3) {
				final double difference = 0.66 * (lastDist - 0.26);
				speed = lastDist - difference;
			} else {
				final List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
				if ((collidingList.size() > 0 || mc.player.collidedVertically) && stage > 0) {
					stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
				}
				speed = lastDist - lastDist / 159.0;
			}
			speed = Math.max(speed, 0.25);

			// Stage checks if you're greater than 0 as step sets you -6 stage to make sure the player won't flag.
			if (stage > 0) {
				// Set strafe motion.
				MathUtils.setSpeed(speed);
			}
			// If the player is moving, step the stage up.
			if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
				++stage;
			}
		} else if (mode.getSelected() == Mode.OTHERNCP) {
			if (mc.player.collidedHorizontally) {
				collided = true;
			}
			if (collided) {
				//mc.timer.tickLength = 50.0F; // Adjust this value as needed.
				stage = -1;
			}
			if (stair > 0)
				stair -= 0.25;
			less -= less > 1 ? 0.12 : 0.11;
			if (less < 0)
				less = 0;
			if (mc.player.onGround && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				collided = mc.player.collidedHorizontally;
				if (stage >= 0 || collided) {
					stage = 0;

					double motY = 0.407;
					if (stair == 0) {
						mc.player.jump();
						mc.player.motionY = motY;
					} else {
						// Handle stair logic here if needed.
					}

					less++;
					if (less > 1 && !lessSlow)
						lessSlow = true;
					else
						lessSlow = false;
					if (less > 1.12)
						less = 1.12;
				}
			}
			speed = getOtherNCPSpeed(stage) + 0.0331;
			speed *= 0.91;
			if (stair > 0) {
				speed *= 0.7;
			}

			if (stage < 0)
				speed = 0.26;
			if (lessSlow) {
				speed *= 0.95;
			}

			if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
				MathUtils.setSpeed(speed);
				++stage;
			}
		} else if (mode.getSelected() == Mode.AAC) {
			if (mc.player.collidedVertically && mc.player.onGround && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
				stage = 0;
				mc.player.jump();
				mc.player.motionY = 0.41999998688698;
				if (aacCount < 4)
					aacCount++;
			}
			speed = getAACSpeed(stage, aacCount);
			if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
				MathUtils.setSpeed(speed);
			}

			if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
				++stage;
			}
		} else if (mode.getSelected() == Mode.AACWALL) {
			if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) {
				if (count > 0) {
					mc.player.motionX *= 0.2;
					mc.player.motionZ *= 0.2;
					count = 0;
				}
				air = 0;
				return;
			}
			if (mc.player.collidedHorizontally) {
				if (mc.player.onGround) {
					mc.player.onGround = true;
					mc.player.motionY = 0.42;
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
					double speed = 0;
					if (count == 0) {
						speed = 0.37;
					} else if (count >= 1) {
						speed = 0.575;
					}
					if (!mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY + 2, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
						if (count >= 1) {
							speed = 0.472;
						}
					}
					MathUtils.setSpeed(speed - 0.005);
					if (count < 2) {
						count++;
					}
					air = 0;
				} else {
					mc.player.motionY = -0.21;
					mc.player.motionX = 0;
					mc.player.motionZ = 0;
					double speed = 0;
					if (air == 0) {
						if (count == 1) {
							speed = 0.277;
						} else if (count == 2) {
							speed = 0.339;
						}
					} else if (air == 1) {
						if (count == 1) {
							speed = 0.275;
						} else if (count == 2) {
							speed = 0.336;
						}
					}
					if (!mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY + 2, mc.player.lastTickPosZ)).getBlock().equals(Blocks.AIR)) {
						if (count == 2) {
							speed = 0.3;
						}
					}
					MathUtils.setSpeed(speed - 0.005);
					air++;
				}
			} else {
				if (count > 0) {
					mc.player.motionX *= 0.2;
					mc.player.motionZ *= 0.2;
					air = 0;
					count = 0;
				}
			}
		} else if (mode.getSelected() == Mode.OLDHOP) {
			if ((mc.player.moveForward == 0.0F) && (mc.player.moveStrafing == 0.0F)) {
				speed = 0.26;
			}
			if ((stage == 1) && (mc.player.collidedVertically) && ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F))) {
				speed = (0.25D + 0.26 - 0.01D);
			} else if ((stage == 2) && (mc.player.collidedVertically) && mc.player.onGround && ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F))) {
				mc.player.motionY = 0.4D;
				mc.player.jump();
				speed *= 2.149D;
			} else if (stage == 3) {
				double difference = 0.66D * (this.lastDist - 0.26);
				speed = (this.lastDist - difference);
			} else {
				List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D));
				if ((collidingList.size() > 0) || (mc.player.collidedVertically)) {
					if (stage > 0) {
						if (1.35D * 0.26 - 0.01D > speed) {
							stage = 0;
						} else {
							stage = (mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F) ? 1 : 0;
						}
					}
				}
				speed = (this.lastDist - this.lastDist / 159.0D);
			}
			speed = Math.max(speed, 0.26);
			if (stage > 0) {
				MathUtils.setSpeed(speed);
			}
			if ((mc.player.moveForward != 0.0F) || (mc.player.moveStrafing != 0.0F)) {
				stage += 1;
			}
		} else if (mode.getSelected() == Mode.ONGROUND) {
			double forward = mc.player.movementInput.moveForward;
			double strafe = mc.player.movementInput.moveStrafe;

			if ((forward != 0 || strafe != 0) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isInWater() && !mc.player.isOnLadder() && !mc.player.collidedHorizontally) {
				double yOffset = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, 0.4D, 0.0D)).isEmpty() ? (mc.player.ticksExisted % 2 != 0 ? 0.2 : 0) : (mc.player.ticksExisted % 2 != 0 ? 0.4198 : 0);
				mc.player.setPosition(mc.player.posX, mc.player.posY + yOffset, mc.player.posZ);
			}

			double speed = mc.player.ticksExisted % 2 == 0 ? 2.1 : 1.3;
			float yaw = mc.player.rotationYaw;

			if ((forward == 0.0D) && (strafe == 0.0D)) {
				mc.player.motionX = 0.0D;
				mc.player.motionZ = 0.0D;
			} else {
				if (forward != 0.0D) {
					if (strafe > 0.0D) {
						yaw += (forward > 0.0D ? -45 : 45);
					} else if (strafe < 0.0D) {
						yaw += (forward > 0.0D ? 45 : -45);
					}
					strafe = 0.0D;
					if (forward > 0.0D) {
						forward = 0.15;
					} else if (forward < 0.0D) {
						forward = -0.15;
					}
				}
				if (strafe > 0) {
					strafe = 0.15;
				} else if (strafe < 0) {
					strafe = -0.15;
				}
				double motionX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
				double motionZ = (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
				mc.player.motionX = motionX;
				mc.player.motionZ = motionZ;
			}
		} else if (mode.getSelected() == Mode.NCPBASIC) {
			if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
				if (mc.player.onGround) {
					mc.player.jump();
				} else {
					MathUtils.setSpeed(0.26);
				}
			}
		}
	}

	private double getOtherNCPSpeed(int stage) {
		double value = 0.26 / 15;
		double firstValue = 0.4145 / 12.5;
		double decr = (((double) stage / 500) * 2);

		if (stage == 0) {
			value = 0.64 * 0.134;
		} else if (stage == 1) {
			// mc.timer.timerSpeed = 1.354f; // Adjust timer speed as needed.
			value = firstValue;
		} else if (stage >= 2) {
			// mc.timer.timerSpeed = 1.254f; // Adjust timer speed as needed.
			value = firstValue - decr;
		}
		if (collided) {
			value = 0.2;
			if (stage == 0)
				value = 0;
		}

		return Math.max(value, 0.26);
	}

	private double getAACSpeed(int stage, int jumps) {
		double value = 0.29;
		double firstvalue = 0.3019;
		double thirdvalue = 0.0286 - (double) stage / 1000;
		if (stage == 0) {
			//JUMP
			value = 0.497;
			if (jumps >= 2) {
				value += 0.1069;
			}
			if (jumps >= 3) {
				value += 0.046;
			}
			Block block = mc.world.getBlockState(new BlockPos(mc.player.lastTickPosX, mc.player.lastTickPosY - 1, mc.player.lastTickPosZ)).getBlock();
			if (block instanceof BlockIce || block instanceof BlockPackedIce) {
				value = 0.59;
			}
		} else if (stage == 1) {
			value = 0.3031;
			if (jumps >= 2) {
				value += 0.0642;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 2) {
			value = 0.302;
			if (jumps >= 2) {
				value += 0.0629;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 3) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0607;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 4) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0584;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 5) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0561;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 6) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0539;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 7) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0517;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 8) {
			value = firstvalue;
			if (mc.player.onGround)
				value -= 0.002;

			if (jumps >= 2) {
				value += 0.0496;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 9) {
			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0475;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 10) {

			value = firstvalue;
			if (jumps >= 2) {
				value += 0.0455;
			}
			if (jumps >= 3) {
				value += thirdvalue;
			}
		} else if (stage == 11) {

			value = 0.3;
			if (jumps >= 2) {
				value += 0.045;
			}
			if (jumps >= 3) {
				value += 0.018;
			}

		} else if (stage == 12) {
			value = 0.301;
			if (jumps <= 2)
				aacCount = 0;
			if (jumps >= 2) {
				value += 0.042;
			}
			if (jumps >= 3) {
				value += thirdvalue + 0.001;
			}
		} else if (stage == 13) {
			value = 0.298;
			if (jumps >= 2) {
				value += 0.042;
			}
			if (jumps >= 3) {
				value += thirdvalue + 0.001;
			}
		} else if (stage == 14) {

			value = 0.297;
			if (jumps >= 2) {
				value += 0.042;
			}
			if (jumps >= 3) {
				value += thirdvalue + 0.001;
			}
		}
		if (mc.player.moveForward <= 0) {
			value -= 0.06;
		}

		if (mc.player.collidedHorizontally) {
			value -= 0.1;
			aacCount = 0;
		}
		return value;
	}

	private enum Mode {
		BHOP("BHop"),
		OTHERNCP("OtherNCP"),
		AAC("AAC"),
		OLDHOP("OldHop"),
		AACWALL("AACWall"),
		ONGROUND("OnGround"),
		NCPBASIC("NCPBasic");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
}
