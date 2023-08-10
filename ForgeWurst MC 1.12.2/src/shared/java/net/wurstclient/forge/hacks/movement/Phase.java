/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.*;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.MathUtils;

public final class Phase extends Hack {

	private float yaw;
	private float pitch;
	private boolean shouldSpeed;
	private int rot1;
	private int rot2;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NCP);

	public Phase() {
		super("Phase", "Allows you too go through blocks.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		shouldSpeed = isInsideBlock();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected() == Mode.NCP) {
			if (isInsideBlock()) {
				mc.player.rotationYaw = yaw;
				mc.player.rotationPitch = pitch;
			} else {
				yaw = mc.player.rotationYaw;
				pitch = mc.player.rotationPitch;
			}
			if (shouldSpeed || isInsideBlock()) {
				if (!mc.player.isSneaking())
					mc.player.prevPosY = 0;
				mc.player.prevRotationPitch = 999;
				mc.player.onGround = false;
				mc.player.noClip = true;
				mc.player.motionX = 0;
				mc.player.motionZ = 0;
				if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.posY == (int) mc.player.posY)
					mc.player.jump();

				mc.player.jumpMovementFactor = 0;
			}

			rot1++;
			if (rot1 < 3) {
				if (rot1 == 1) {
					pitch += 15;
				} else {
					pitch -= 15;
				}
			}

			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.player.prevRotationPitch = 999;
				double X = mc.player.posX;
				double Y = mc.player.posY;
				double Z = mc.player.posZ;
				if (!isMoving()) {
					if (mc.player.onGround && !isInsideBlock()) {
						mc.player.prevPosY = -99;
						mc.player.setPosition(mc.player.posX, Y - 1, mc.player.posZ);
						mc.player.setPosition(X, Y - 1, Z);
						mc.player.motionY = 0;
					} else if (mc.player.ticksExisted % 5 == 0 && mc.player.posY == (int) mc.player.posY) {
						mc.player.setPosition(X, Y - 0.3, Z);
					}
				}
			}

			if (isInsideBlock() && rot1 >= 3) {
				if (shouldSpeed) {
					teleport(0.617);

					float sin = (float) Math.sin(rot2) * 0.1f;
					float cos = (float) Math.cos(rot2) * 0.1f;
					mc.player.rotationYaw += sin;
					mc.player.rotationPitch += cos;
					rot2++;
				} else {
					teleport(0.031);
				}
			}
		} else if (mode.getSelected() == Mode.SILENT) {
			final double mx = Math.cos(Math.toRadians(mc.player.rotationYaw + 90.0f));
			final double mz = Math.sin(Math.toRadians(mc.player.rotationYaw + 90.0f));
			final double x = mc.player.movementInput.moveForward * 0.155 * mx + mc.player.movementInput.moveStrafe * 0.155 * mz;
			final double z = mc.player.movementInput.moveForward * 0.155 * mz - mc.player.movementInput.moveStrafe * 0.155 * mx;
			if (mc.player.collidedHorizontally && !mc.player.isOnLadder() && !isInsideBlock()) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY, mc.player.posZ + z, false));
				for (int i = 1; i < 10; ++i) {
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 8.988465674311579E307, mc.player.posZ, false));
				}
				mc.player.setPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z);
			}
		}
	}

	public boolean isInsideBlock() {
		for (int x = MathHelper.floor(mc.player.getEntityBoundingBox().minX); x < MathHelper.floor(mc.player.getEntityBoundingBox().maxX) + 1; x++) {
			for (int y = MathHelper.floor(mc.player.getEntityBoundingBox().minY); y < MathHelper.floor(mc.player.getEntityBoundingBox().maxY) + 1; y++) {
				for (int z = MathHelper.floor(mc.player.getEntityBoundingBox().minZ); z < MathHelper.floor(mc.player.getEntityBoundingBox().maxZ) + 1; z++) {
					Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (!(block instanceof BlockAir)) {
						AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.world.getBlockState(new BlockPos(x, y, z)), mc.world, new BlockPos(x, y, z));
						if ((block instanceof BlockHopper)) {
							boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
						}
						if (boundingBox != null) {
							if (mc.player.getEntityBoundingBox().intersects(boundingBox)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void teleport(double dist) {
		double forward = mc.player.movementInput.moveForward;
		double strafe = mc.player.movementInput.moveStrafe;
		float yaw = mc.player.rotationYaw;
		if (forward != 0.0D) {
			if (strafe > 0.0D) {
				yaw += (forward > 0.0D ? -45 : 45);
			} else if (strafe < 0.0D) {
				yaw += (forward > 0.0D ? 45 : -45);
			}
			strafe = 0.0D;
			if (forward > 0.0D) {
				forward = 1;
			} else if (forward < 0.0D) {
				forward = -1;
			}
		}
		double x = mc.player.posX; double y = mc.player.posY; double z = mc.player.posZ;
		double xspeed = forward * dist * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * dist * Math.sin(Math.toRadians(yaw + 90.0F));
		double zspeed = forward * dist * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * dist * Math.cos(Math.toRadians(yaw + 90.0F));
		mc.player.setPosition(x + xspeed, y, z + zspeed);
	}

	private boolean isMoving() {
		return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown();
	}

	private enum Mode
	{
		NCP("NCP"),
		SILENT("Silent");

		private final String name;

		private Mode(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}