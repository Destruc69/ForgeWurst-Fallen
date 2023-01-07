/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.CrystalUtil;
import net.wurstclient.forge.utils.RotationUtils;

import java.util.ArrayList;

public final class AutoCrystal extends Hack {

	private final SliderSetting radius =
			new SliderSetting("Radius", "Radius around the target player", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting distance =
			new SliderSetting("Distance", "Min distance to target player", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting speed =
			new SliderSetting("Speed", "Speed of AC (FASTER <-> SLOWER)", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Packets", Mode.values(), Mode.PACKET);

	private final SliderSetting pstength =
			new SliderSetting("PacketStrength", "Strength of packets.", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting maxSelfDamage =
			new SliderSetting("MaxSelfDamage", "If crystal will exeed self damage, return.", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting minTargDamage =
			new SliderSetting("MinTargDamage", "If crystal will not damage the target enough, return.", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private enum Mode {
		NONE("None", true, false, false),
		PACKET("Packet", false, false, false),
		PACKETPLUS("PacketPlus", false, false, false);

		private final String name;
		private final boolean none;
		private final boolean packet;
		private final boolean packetplus;

		private Mode(String name, boolean none, boolean packet, boolean packetplus) {
			this.name = name;
			this.none = none;
			this.packet = packet;
			this.packetplus = packetplus;
		}

		public String toString() {
			return name;
		}
	}

	public static ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();

	public static EntityPlayer targetPlayer = null;

	public AutoCrystal() {
		super("AutoCrystal", "Auto Crystal but for Killaura.");
		setCategory(Category.COMBAT);
		addSetting(radius);
		addSetting(distance);
		addSetting(speed);
		addSetting(mode);
		addSetting(pstength);
		addSetting(maxSelfDamage);
		addSetting(minTargDamage);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		targetPlayer = null;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		targetPlayer = null;
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
			for (Entity entity : mc.world.loadedEntityList) {
				if (entity instanceof EntityPlayer) {
					if (entity != mc.player) {
						if (mc.player.getDistance(entity) <= distance.getValueF()) {
							targetPlayer = (EntityPlayer) entity;
						}
					}
				}
			}

			if (targetPlayer != null) {
				for (int x = -radius.getValueI(); x < radius.getValueF(); x++) {
					for (int z = -radius.getValueI(); z < radius.getValueF(); z++) {
						Entity entity = targetPlayer;
						if (mc.world.getBlockState(entity.getPosition().add(x, 0, z)).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(entity.getPosition().add(x, -1, z)).getBlock().equals(Blocks.BEDROCK))) {
							blockPosArrayList.add(entity.getPosition().add(x, -1, z));
						}
					}
				}
			}

			if (targetPlayer != null) {
				if (mc.player.ticksExisted % speed.getValueF() == 0) {
					BlockPos blockPos = blockPosArrayList.get(blockPosArrayList.size() - 1);
					placeCrystal(blockPos);
				}
				for (Entity entity : mc.world.loadedEntityList) {
					if (entity instanceof EntityEnderCrystal) {
						if (mc.player.getDistance(entity) <= distance.getValueF()) {
							if (mc.player.ticksExisted % 5 == 0) {
								breakCrystal((EntityEnderCrystal) entity);
							}
						}
					}
				}
			}
		}
	}

	public void breakCrystal(EntityEnderCrystal entityEnderCrystal) {
		if (CrystalUtil.calculateDamage(new Vec3d(entityEnderCrystal.lastTickPosX, entityEnderCrystal.lastTickPosY, entityEnderCrystal.posZ).addVector(0.5, 0.5, 0.5), mc.player) > maxSelfDamage.getValueF())
			return;
		if (CrystalUtil.calculateDamage(new Vec3d(entityEnderCrystal.lastTickPosX, entityEnderCrystal.lastTickPosY, entityEnderCrystal.posZ).addVector(0.5, 0.5, 0.5), targetPlayer) < minTargDamage.getValueF())
			return;
		assert entityEnderCrystal != null;
		for (int x = 0; x < pstength.getValueF(); x ++) {
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(entityEnderCrystal.lastTickPosX, entityEnderCrystal.lastTickPosY, entityEnderCrystal.lastTickPosZ));
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
		}
		mc.playerController.attackEntity(mc.player, entityEnderCrystal);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		if (mode.getSelected().packet || mode.getSelected().packetplus) {
			handleAttackPacket(entityEnderCrystal);
		}
	}

	public void placeCrystal(BlockPos blockPos) {
		if (CrystalUtil.calculateDamage(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).addVector(0.5, 0.5, 0.5), mc.player) > maxSelfDamage.getValueF())
			return;
		if (CrystalUtil.calculateDamage(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).addVector(0.5, 0.5, 0.5), targetPlayer) < minTargDamage.getValueF())
			return;
		for (int x = 0; x < pstength.getValueF(); x ++) {
			float[] rot = RotationUtils.getNeededRotations(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
		}
		mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, EnumFacing.UP, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
		mc.player.swingArm(EnumHand.MAIN_HAND);
		if (mode.getSelected().packet || mode.getSelected().packetplus) {
			handPlacePacket(blockPos);
		}
	}

	public void handleAttackPacket(Entity entity) {
		if (CrystalUtil.calculateDamage(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.posZ).addVector(0.5, 0.5, 0.5), mc.player) > maxSelfDamage.getValueF())
			return;
		if (CrystalUtil.calculateDamage(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.posZ).addVector(0.5, 0.5, 0.5), targetPlayer) < minTargDamage.getValueF())
			return;
		for (int x = 0; x < pstength.getValueF(); x ++) {
			float f = (float) (entity.getPositionVector().x - (double) entity.getPosition().getX());
			float f1 = (float) (entity.getPositionVector().y - (double) entity.getPosition().getY());
			float f2 = (float) (entity.getPositionVector().z - (double) entity.getPosition().getZ());
			mc.player.connection.sendPacket(new CPacketUseEntity(entity, EnumHand.MAIN_HAND, new Vec3d(f, f1, f2)));
			if (mode.getSelected().packetplus) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ, true));
			}
		}
	}

	public void handPlacePacket(BlockPos blockPos) {
		if (CrystalUtil.calculateDamage(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).addVector(0.5, 0.5, 0.5), mc.player) > maxSelfDamage.getValueF())
			return;
		if (CrystalUtil.calculateDamage(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).addVector(0.5, 0.5, 0.5), targetPlayer) < minTargDamage.getValueF())
			return;
		for (int x = 0; x < pstength.getValueF(); x++) {
			mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos, EnumFacing.UP, EnumHand.MAIN_HAND, blockPos.getX() - 0.5f, blockPos.getY() - 0.5f, blockPos.getZ() - 0.5f));
			if (mode.getSelected().packetplus) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(blockPos.getX(), blockPos.getY(), blockPos.getY(), true));
			}
		}
	}
}