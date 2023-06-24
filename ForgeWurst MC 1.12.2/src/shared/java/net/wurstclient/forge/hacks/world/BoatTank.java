/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.InventoryUtil;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BoatTank extends Hack {

	public BoatTank() {
		super("BoatTank", "Turns your boat into a tank.");
		setCategory(Category.WORLD);
	}

	private ArrayList<Entity> entities;
	private ArrayList<BlockPos> blockPosArrayList;

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		entities = new ArrayList<>();
		entities.clear();

		blockPosArrayList = new ArrayList<>();
		blockPosArrayList.clear();
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		entities = new ArrayList<>();
		entities.clear();

		blockPosArrayList = new ArrayList<>();
		blockPosArrayList.clear();
	}

	private static final int FORCE_BAR_WIDTH = 100;
	private static final int FORCE_BAR_HEIGHT = 10;

	private boolean isCharging = false;
	private int chargingTicks = 0;
	private int maxChargingTicks = 40;
	private double maxForce = 5.0;


	// I will do more than just this later.

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		double force = (double) chargingTicks / maxChargingTicks * maxForce;

		if (mc.player.getRidingEntity() instanceof EntityBoat) {

			for (int x = -1; x < 1; x++) {
				for (int z = -1; z < 1; z++) {
					BlockPos blockPos = new BlockPos(mc.player.getRidingEntity().lastTickPosX + x, mc.player.getRidingEntity().lastTickPosY, mc.player.lastTickPosZ + z);
					if (!(mc.world.getBlockState(blockPos)).getBlock().equals(Blocks.AIR)) {
						mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.DOWN);
						mc.player.getRidingEntity().setVelocity(0, mc.player.getRidingEntity().motionY, 0);
					} else {
						if (!mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown()) {
							double[] spd = MathUtils.directionSpeed(0.4);
							mc.player.getRidingEntity().setVelocity(spd[0], mc.player.getRidingEntity().motionY, spd[1]);
						} else {
							mc.player.setVelocity(0, mc.player.getRidingEntity().motionY, 0);
						}
					}
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (!isCharging) {
					isCharging = true;
					chargingTicks = 0;
				}
				chargingTicks++;
				chargingTicks = Math.min(chargingTicks, maxChargingTicks);
			} else if (isCharging) {
				if (hasArrows()) {

					double yawRadians = Math.toRadians(mc.player.rotationYaw);
					double pitchRadians = Math.toRadians(mc.player.rotationPitch);

					double x = -Math.sin(yawRadians) * Math.cos(pitchRadians);
					double y = -Math.sin(pitchRadians);
					double z = Math.cos(yawRadians) * Math.cos(pitchRadians);

					Vec3d motionVector = new Vec3d(x, y, z).normalize().scale(force);

					EntityTippedArrow entityArrow = new EntityTippedArrow(mc.world, mc.player);
					entityArrow.shoot(motionVector.x, motionVector.y, motionVector.z, (float) force, (float) force * 2);
					entityArrow.setPosition(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
					entityArrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
					entityArrow.setDamage(force);
					entityArrow.setKnockbackStrength((int) force);
					entityArrow.arrowShake = (int) force;
					entityArrow.setIsCritical(true);

					entityArrow.setDamage(force);

					int slott = InventoryUtil.getSlot(Items.ARROW);
					entityArrow.setPotionEffect(mc.player.inventory.getStackInSlot(slott));

					mc.world.spawnEntity(entityArrow);
					entities.add(entityArrow);

					isCharging = false;
					chargingTicks = 0;

					int slot = InventoryUtil.getSlot(Items.ARROW);
					if (mc.player.inventory.getStackInSlot(slot).getCount() - 1 >= 0) {
						mc.player.inventory.getStackInSlot(slot).setCount(mc.player.inventory.getStackInSlot(slot).getCount() - 1);
					}
				}
			}
		}

		for (Entity entity : entities) {
			if (!mc.world.getBlockState(entity.getPosition().add(0, -0.1, 0)).getBlock().equals(Blocks.AIR)) {
				mc.world.createExplosion(mc.player, entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ, 3, true);
				mc.world.removeEntity(entity);
			}
		}

		try {
			for (BlockPos blockPos : blockPosArrayList) {
				if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 5) {
					mc.playerController.onPlayerDestroyBlock(blockPos);
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		if (isCharging) {
			int screenWidth = event.getResolution().getScaledWidth();
			int screenHeight = event.getResolution().getScaledHeight();

			int barX = screenWidth / 2 - FORCE_BAR_WIDTH / 2;
			int barY = screenHeight - 50;

			double progress = (double) chargingTicks / maxChargingTicks;
			int barProgress = (int) (progress * FORCE_BAR_WIDTH);

			// Render the background of the bar
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(barX, barY);
			GL11.glVertex2d(barX, barY + FORCE_BAR_HEIGHT);
			GL11.glVertex2d(barX + FORCE_BAR_WIDTH, barY + FORCE_BAR_HEIGHT);
			GL11.glVertex2d(barX + FORCE_BAR_WIDTH, barY);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

			// Render the filled part of the bar
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.8f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(barX, barY);
			GL11.glVertex2d(barX, barY + FORCE_BAR_HEIGHT);
			GL11.glVertex2d(barX + barProgress, barY + FORCE_BAR_HEIGHT);
			GL11.glVertex2d(barX + barProgress, barY);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();

			if (!hasArrows()) {
				FontRenderer fontRenderer = mc.fontRenderer;
				String text = "Out of arrows!"; // The text you want to render
				int x = barX; // The x-coordinate of the text
				int y = barY - 20; // The y-coordinate of the text
				int color = 0xFFFFFF; // The color of the text (white in this example)

				fontRenderer.drawStringWithShadow(text, x, y, color);
			}
		}
	}

	private boolean hasArrows() {
		return mc.player.inventory.hasItemStack(new ItemStack(Items.ARROW));
	}
}
