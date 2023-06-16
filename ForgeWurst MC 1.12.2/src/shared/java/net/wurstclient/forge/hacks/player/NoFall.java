/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.FallenRenderUtils;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RotationUtils;

import java.awt.*;
import java.util.Objects;

public final class NoFall extends Hack {

	private final SliderSetting fallDistance =
			new SliderSetting("FallDistance [ANTIFALL]", "If we exeed this value we will prevent you from falling [FOR ANTI-FALL]", 4, 1, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	public static double lastOnGroundX = 0;
	public static double lastOnGroundY = 0;
	public static double lastOnGroundZ = 0;

	public static double lastOnGroundXR = 0;
	public static double lastOnGroundYR = 0;
	public static double lastOnGroundZR = 0;

	public static Vec3d lastOnGroundLeave = new Vec3d(0, 0, 0);

	private final EnumSetting<ModeRideable> modeRideable =
			new EnumSetting<>("Rideables", ModeRideable.values(), ModeRideable.DAMAGE);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.PACKET);

	public NoFall() {
		super("NoFall", "Prevents falling damage/falling.");
		setCategory(Category.PLAYER);
		addSetting(mode);
		addSetting(fallDistance);
		addSetting(modeRideable);
	}

	@Override
	public String getRenderName() {
		return getName() + " [" + mode.getSelected().name() + "]";
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			if (mode.getSelected().packet) {
				if (mc.player.fallDistance > 4) {
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			}
			if (mode.getSelected().waterbucket) {
				if (mc.world != null && mc.player != null) {
					for (int a = 0; a < 3; a++) {
						BlockPos blockPos = new BlockPos(mc.player.lastTickPosX, mc.player.posY - a, mc.player.lastTickPosZ);
						if (!(blockPos == null)) {
							mc.playerController.updateController();
							if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem().equals(Items.BUCKET)) {
								mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
								mc.player.swingArm(EnumHand.MAIN_HAND);
							}
							if (mc.player.onGround) {
								lastOnGroundY = mc.player.posY;
								double savedSlot = mc.player.inventory.currentItem;
								if (mc.player.inventory.currentItem == InventoryUtil.getSlot(Items.WATER_BUCKET)) {
									mc.player.inventory.currentItem = (int) savedSlot;
									mc.playerController.updateController();
									KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, false);
								}
							} else {
								if (mc.player.getDistance(mc.player.lastTickPosX, lastOnGroundY, mc.player.lastTickPosZ) > 4) {
									Vec3d centerPlayerPos = new Vec3d(Math.round(mc.player.posX) + 0.5, Math.round(mc.player.posY) - 1, Math.round(mc.player.posZ) + 0.5);
									float[] rot = RotationUtils.getNeededRotations(centerPlayerPos);
									mc.player.rotationYaw = rot[0];
									mc.player.rotationPitch = 90;
									KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, true);
									if (!(mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR))) {
										double newSlot = InventoryUtil.getSlot(Items.WATER_BUCKET);
										mc.player.inventory.currentItem = (int) newSlot;
										mc.playerController.updateController();

										mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
										mc.player.swingArm(EnumHand.MAIN_HAND);
									}
								}
							}
						}
					}
				}
			}
			if (mode.getSelected().aac) {
				if (mc.player.fallDistance > 2) {
					mc.player.motionZ = 0;
					mc.player.motionX = mc.player.motionZ;
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10E-4, mc.player.posZ, mc.player.onGround));
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			}

			if (mode.getSelected().anti) {
				if (mc.player.onGround) {
					lastOnGroundX = mc.player.lastTickPosX;
					lastOnGroundY = mc.player.lastTickPosY;
					lastOnGroundZ = mc.player.lastTickPosZ;
				}
				if (mc.player.fallDistance > fallDistance.getValueF()) {
					mc.player.setPosition(lastOnGroundX, lastOnGroundY, lastOnGroundZ);
				}
			}
			if (mode.getSelected().damage) {
				if (mc.player.fallDistance > 4) {
					mc.player.onGround = true;
				}
			}

			try {
				if (modeRideable.getSelected().damage) {
					if (Objects.requireNonNull(mc.player.getRidingEntity()).fallDistance > 4) {
						Objects.requireNonNull(mc.player.getRidingEntity()).onGround = true;
					}
				} else if (modeRideable.getSelected().anti) {
					if (Objects.requireNonNull(mc.player.getRidingEntity()).onGround) {
						lastOnGroundXR = mc.player.getRidingEntity().lastTickPosX;
						lastOnGroundYR = mc.player.getRidingEntity().lastTickPosY;
						lastOnGroundZR = mc.player.getRidingEntity().lastTickPosZ;
					}
					if (mc.player.getRidingEntity().fallDistance > fallDistance.getValueF()) {
						mc.player.getRidingEntity().setPosition(lastOnGroundXR, lastOnGroundYR, lastOnGroundZR);
					}
				}
			} catch (Exception ignored) {
			}

			try {
				if (mode.getSelected().leave) {
					if (mc.player.onGround) {
						lastOnGroundLeave = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
					} else {
						if (!mc.world.getBlockState(mc.player.getPosition().add(0, -2, 0)).getBlock().equals(Blocks.AIR) && mc.player.getDistance(mc.player.lastTickPosX, lastOnGroundLeave.y, mc.player.lastTickPosZ) > 4) {
							mc.player.connection.onDisconnect(new TextComponentString("[LEAVE-NOFALL] Rejoin and your fall distance will be dismissed."));
						}
					}
				}
			} catch (Exception ignored) {
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (mode.getSelected().waterbucket) {
			if (mc.player.getDistance(mc.player.lastTickPosX, lastOnGroundY, mc.player.lastTickPosZ) > 4) {
				FallenRenderUtils.drawLine(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ), new Vec3d(Math.round(mc.player.lastTickPosX) + 0.5, Math.round(mc.player.lastTickPosY - 3), Math.round(mc.player.lastTickPosZ) + 0.5), 2, Color.GREEN);
			}
		}
	}

	private enum Mode {
		PACKET("Packet", true, false, false, false, false, false),
		ANTI("Anti", false, true, false, false, false, false),
		DAMAGE("Damage", false, false, false, true, false, false),
		AAC("AAC", false, false, true, false, false, false),
		LEAVE("Leave", false, false, false, false, true, false),
		WATERBUCKET("WaterBucket", false, false, false, false, false, true);

		private final String name;
		private final boolean packet;
		private final boolean anti;
		private final boolean aac;
		private final boolean damage;
		private final boolean leave;
		private final boolean waterbucket;

		private Mode(String name, boolean packet, boolean anti, boolean aac, boolean damage, boolean leave, boolean waterbucket) {
			this.name = name;
			this.anti = anti;
			this.packet = packet;
			this.aac = aac;
			this.damage = damage;
			this.leave = leave;
			this.waterbucket = waterbucket;
		}

		public String toString() {
			return name;
		}
	}

	private enum ModeRideable {
		DAMAGE("Damage", true, false, false),
		ANTI("Anti", false, true, false),
		OFF("Off", false, false, true);

		private final String name;
		private final boolean damage;
		private final boolean anti;
		private final boolean off;

		private ModeRideable(String name, boolean damage, boolean anti, boolean off) {
			this.name = name;
			this.anti = anti;
			this.damage = damage;
			this.off = off;
		}

		public String toString() {
			return name;
		}
	}
}