/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AutoCrystal extends Hack {

	private final SliderSetting range = new SliderSetting("Range",
			"Determines how far CrystalAura will reach to place and detonate crystals.",
			5, 1, 5, 0.5, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting autoPlace = new CheckboxSetting(
			"Auto-place crystals",
			"When enabled, CrystalAura will automatically place crystals near valid entities.\n"
					+ "When disabled, CrystalAura will only detonate manually placed crystals.",
			true);

	public AutoCrystal() {
		super("AutoCrystal", "Auto Crystal but for Killaura.");
		setCategory(Category.COMBAT);
		addSetting(range);
		addSetting(autoPlace);
	}

	//
	// WURST-7 CRYSTALAURA
	//

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
		ArrayList<Entity> crystals = getNearbyCrystals();

		if (!crystals.isEmpty()) {
			detonate(crystals);
			return;
		}

		if (!autoPlace.isChecked()) {
			return;
		}

		ArrayList<Entity> targets = getNearbyTargets();
		placeCrystalsNear(targets);
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		Packet<?> packet = event.getPacket();
		if (packet instanceof SPacketSoundEffect) {
			final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) packet;
			if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
				for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
					if (entity instanceof EntityEnderCrystal) {
						if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= mc.gameSettings.renderDistanceChunks * 16) {
							entity.setDead();
						}
					}
				}
			}
		}
	}

	private void placeCrystalsNear(ArrayList<Entity> targets) {
		boolean shouldSwing = false;
		for (Entity target : targets) {
			ArrayList<BlockPos> freeBlocks = getFreeBlocksNear(target);

			for (BlockPos pos : freeBlocks) {
				if (placeCrystal(pos)) {
					shouldSwing = true;
					break;
				}
			}
		}

		if (shouldSwing) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}

	private void detonate(ArrayList<Entity> crystals) {
		for (Entity e : crystals) {

			float[] rot = RotationUtils.getNeededRotations(new Vec3d(e.lastTickPosX + 0.5, e.lastTickPosY + 0.5, e.lastTickPosZ + 0.5));
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));

			mc.playerController.attackEntity(mc.player, e);
		}

		if (!crystals.isEmpty()) {
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}

	private boolean placeCrystal(BlockPos pos) {
		Vec3d eyesPos = RotationUtils.getEyesPos();
		double rangeSq = Math.pow(range.getValue(), 2);
		Vec3d posVec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(side);

			// check if neighbor can be right clicked
			if (!isClickableNeighbor(neighbor)) {
				continue;
			}

			Vec3d dirVec = new Vec3d(side.getDirectionVec());
			Vec3d hitVec = posVec.add(dirVec.scale(0.5));

			// check if hitVec is within range
			if (eyesPos.squareDistanceTo(hitVec) > rangeSq) {
				continue;
			}

			// check if side is visible (facing away from player)
			if (distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec))) {
				continue;
			}

			mc.player.inventory.currentItem = getSlot(Items.END_CRYSTAL);
			mc.playerController.updateController();

			float[] rot = RotationUtils.getNeededRotations(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));

			// place block
			mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor,
					side.getOpposite(), hitVec, EnumHand.MAIN_HAND);

			return true;
		}

		return false;
	}

	private ArrayList<Entity> getNearbyCrystals() {
		EntityPlayerSP player = mc.player;
		double rangeSq = Math.pow(range.getValue(), 2);

		Comparator<Entity> furthestFromPlayer = Comparator
				.<Entity>comparingDouble(e -> mc.player.getDistanceSq(e))
				.reversed();

		return mc.world.loadedEntityList.parallelStream()
				.filter(e -> e instanceof EntityEnderCrystal)
				.filter(e -> !e.isDead)
				.filter(e -> player.getDistanceSq(e) <= rangeSq)
				.sorted(furthestFromPlayer)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ArrayList<Entity> getNearbyTargets() {
		double rangeSq = Math.pow(range.getValue(), 2);

		Comparator<Entity> furthestFromPlayer = Comparator
				.<Entity>comparingDouble(e -> mc.player.getDistanceSq(e))
				.reversed();

		Stream<Entity> stream = mc.world.loadedEntityList.stream()
				.filter(e -> !e.isDead)
				.filter(e -> e instanceof EntityLivingBase && ((EntityLivingBase) e).getHealth() > 0)
				.filter(e -> e != mc.player)
				.filter(e -> mc.player.getDistanceSq(e) <= rangeSq);

		return stream.sorted(furthestFromPlayer)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ArrayList<BlockPos> getFreeBlocksNear(Entity target) {
		Vec3d eyesVec = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		double rangeSq = Math.pow(range.getValue() + 0.5, 2);
		int rangeI = 2;

		BlockPos center = target.getPosition();
		BlockPos min = center.add(-rangeI, -rangeI, -rangeI);
		BlockPos max = center.add(rangeI, rangeI, rangeI);
		AxisAlignedBB targetBB = target.getEntityBoundingBox();

		Vec3d targetEyesVec = target.getPositionVector()
				.addVector(0, target.getEyeHeight(), 0);

		ArrayList<BlockPos> validPositions = new ArrayList<>();

		for (BlockPos pos : BlockPos.getAllInBox(min, max)) {
			if (eyesVec.squareDistanceTo(new Vec3d(pos)) <= rangeSq
					&& isReplaceable(pos) && hasCrystalBase(pos)
					&& !targetBB.intersects(new AxisAlignedBB(pos))) {
				validPositions.add(pos);
			}
		}

		Comparator<BlockPos> closestToTarget = Comparator
				.<BlockPos>comparingDouble(pos -> targetEyesVec.squareDistanceTo(new Vec3d(pos).addVector(0.5, 0.5, 0.5)));

		validPositions.sort(closestToTarget);

		return validPositions;
	}

	private boolean isReplaceable(BlockPos pos) {
		return mc.world.getBlockState(pos).getMaterial().isReplaceable();
	}

	private boolean hasCrystalBase(BlockPos pos) {
		Block block = mc.world.getBlockState(pos.down()).getBlock();
		return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN;
	}

	private boolean isClickableNeighbor(BlockPos pos) {
		return BlockUtils.canBeClicked(pos)
				&& !mc.world.getBlockState(pos).getMaterial().isReplaceable();
	}

	private int getSlot(Item item) {
		for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
			if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
				return i;
			}
		}
		return 0;
	}
}