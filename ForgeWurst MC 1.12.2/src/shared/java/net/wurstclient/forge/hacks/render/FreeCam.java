/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.*;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.EntityFakePlayer;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class FreeCam extends Hack {
	public static EntityOtherPlayerMP camera;

	private final CheckboxSetting old =
			new CheckboxSetting("OldFreeCam",
					true);

	private final SliderSetting speed =
			new SliderSetting("Speed", 1, 0.05, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting view =
			new CheckboxSetting("ViewPlayer", "Make movies/trailers, just make sure its only 2 players",
					true);

	private final SliderSetting viewx =
			new SliderSetting("View-XCoord", 4, -8, 8, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting viewy =
			new SliderSetting("View-YCoord", 4, -8, 8, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting viewz =
			new SliderSetting("View-ZCoord", 4, -8, 8, 1, SliderSetting.ValueDisplay.DECIMAL);


	private EntityFakePlayer fakePlayer;

	public FreeCam() {
		super("FreeCam", "Go outside of your body.");
		setCategory(Category.RENDER);
		addSetting(old);
		addSetting(speed);
		addSetting(view);
		addSetting(viewx);
		addSetting(viewy);
		addSetting(viewz);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + speed.getValueString() + "]";
	}

	@Override
	public void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (!old.isChecked()) {
			if (mc.player == null || mc.world == null)
				return;


			mc.renderChunksMany = false;

			camera = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
			camera.copyLocationAndAnglesFrom(mc.player);
			camera.prevRotationYaw = mc.player.rotationYaw;
			camera.rotationYawHead = mc.player.rotationYawHead;
			camera.inventory.copyInventory(mc.player.inventory);
			mc.world.addEntityToWorld(-100, camera);
			mc.setRenderViewEntity(camera);
		} else {
			fakePlayer = new EntityFakePlayer();

			GameSettings gs = mc.gameSettings;
			KeyBinding[] bindings = {gs.keyBindForward, gs.keyBindBack,
					gs.keyBindLeft, gs.keyBindRight, gs.keyBindJump, gs.keyBindSneak};
			for(KeyBinding binding : bindings)
				KeyBindingUtils.resetPressed(binding);
		}
	}

	@Override
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		if (!old.isChecked()) {
			mc.renderChunksMany = true;

			if (mc.player != null && mc.world != null && mc.getRenderViewEntity() != null) {
				mc.player.moveStrafing = 0;
				mc.player.moveForward = 0;
				mc.world.removeEntity(camera);
				mc.setRenderViewEntity(mc.player);
			}
		} else {
			fakePlayer.resetPlayerPosition();
			fakePlayer.despawn();

			mc.renderGlobal.loadRenderers();
		}
	}

	@SubscribeEvent
	public void onUpdate(LivingEvent.LivingUpdateEvent e) {
		if (!old.isChecked()) {
			if (!e.getEntity().equals(camera) || mc.currentScreen != null) {
				return;
			}

			if (camera == null)
				return;

			//Update motion
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				camera.motionY = speed.getValueF();
			} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				camera.motionY = -speed.getValueF();
			} else {
				camera.motionY = 0;
			}

			if (mc.gameSettings.keyBindForward.isKeyDown()) {
				camera.moveForward = 1;
			} else if (mc.gameSettings.keyBindBack.isKeyDown()) {
				camera.moveForward = -1;
			} else {
				camera.moveForward = 0;
			}

			if (mc.gameSettings.keyBindLeft.isKeyDown()) {
				camera.moveStrafing = -1;
			} else if (mc.gameSettings.keyBindRight.isKeyDown()) {
				camera.moveStrafing = 1;
			} else {
				camera.moveStrafing = 0;
			}

			if (camera.moveStrafing != 0 || camera.moveForward != 0) {
				double yawRad = Math.toRadians(camera.rotationYaw - getRotationFromVec(new Vec3d(camera.moveStrafing, 0.0, camera.moveForward))[0]);

				camera.motionX = -Math.sin(yawRad) * speed.getValueF();
				camera.motionZ = Math.cos(yawRad) * speed.getValueF();

				if (mc.gameSettings.keyBindSprint.isKeyDown()) {
					camera.setSprinting(true);
					camera.motionX *= 1.5;
					camera.motionZ *= 1.5;
				} else {
					camera.setSprinting(false);
				}
			} else {
				camera.motionX = 0;
				camera.motionZ = 0;
			}

			camera.inventory.copyInventory(mc.player.inventory);
			camera.noClip = true;
			camera.rotationYaw = mc.player.rotationYaw;
			camera.rotationPitch = mc.player.rotationPitch;

			camera.move(MoverType.SELF, camera.motionX, camera.motionY, camera.motionZ);
			e.getEntity().move(MoverType.SELF, camera.motionX, camera.motionY, camera.motionZ);
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (!old.isChecked() && view.isChecked()) {
			old.setChecked(true);
		}
		if (old.isChecked() && !view.isChecked()) {
			EntityPlayerSP player = event.getPlayer();

			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;

			player.onGround = false;
			player.jumpMovementFactor = speed.getValueF();

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValue();

			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValue();
		} else if (view.isChecked() && old.isChecked()) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityPlayer) {
					if (entity != mc.player) {
						if (entity.getName() != event.getPlayer().getName()) {
							BlockPos isCameraInBlock = new BlockPos(entity.posX + viewx.getValueF(), entity.posY + viewy.getValueF() + 1, entity.posZ + viewz.getValueF());
							if (mc.world.getBlockState(isCameraInBlock).getBlock().equals(Blocks.AIR)) {
								mc.player.setPosition(entity.posX + viewx.getValueF(), entity.posY + viewy.getValueF(), entity.posZ + viewz.getValueF());
							} else {
								mc.player.setPosition(entity.posX + viewx.getValueF(), entity.posY + viewy.getValueF() - 1, entity.posZ + viewz.getValueF());
							}
							mc.player.setVelocity(0, 0, 0);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onKeyEvent(InputUpdateEvent event) {
		event.getMovementInput().moveForward = 0;
		event.getMovementInput().moveStrafe = 0;
		event.getMovementInput().jump = false;
		event.getMovementInput().sneak = false;
	}

	@SubscribeEvent
	private void packet(WPacketInputEvent event) {
		if (!old.isChecked()) {
			try {
				if (event.getPacket() instanceof CPacketUseEntity) {
					CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();

					if (packet.getEntityFromWorld(mc.world).equals(mc.player)) {
						event.setCanceled(true);
					}
				}
			} catch (NullPointerException e) {
			}
		}
	}

	public static double[] getRotationFromVec(Vec3d vec) {
		double xz = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
		double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
		double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
		return new double[]{yaw, pitch};
	}

	public static double normalizeAngle(double angle) {
		angle %= 360.0;

		if (angle >= 180.0) {
			angle -= 360.0;
		}

		if (angle < -180.0) {
			angle += 360.0;
		}

		return angle;
	}

	@SubscribeEvent
	public void onPlayerMove(WPlayerMoveEvent event) {
		if (old.isChecked()) {
			event.getPlayer().noClip = true;
		}
	}

	@SubscribeEvent
	public void onIsNormalCube(WIsNormalCubeEvent event) {
		if (old.isChecked()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onSetOpaqueCube(WSetOpaqueCubeEvent event) {
		if (old.isChecked()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPacketOutput(WPacketOutputEvent event) {
		if (old.isChecked()) {
			if (event.getPacket() instanceof CPacketPlayer)
				event.setCanceled(true);
		}
	}
}