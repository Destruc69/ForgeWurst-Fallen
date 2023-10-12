/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.InventoryUtils;
import net.wurstclient.forge.utils.RotationUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrystalAuraHack extends Hack {

	private final SliderSetting range = new SliderSetting("Range",
			"Determines how far CrystalAura will reach to place and detonate crystals.",
			6, 1, 6, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting autoPlace = new CheckboxSetting(
			"Auto-place crystals",
			"When enabled, CrystalAura will automatically place crystals near valid entities.\n"
					+ "When disabled, CrystalAura will only detonate manually placed crystals.",
			true);

	private final CheckboxSetting faceBlocks =
			new CheckboxSetting("Face crystals",
					"Whether or not CrystalAura should face the correct direction when"
							+ " placing and left-clicking end crystals.\n\n"
							+ "Slower but can help with anti-cheat plugins.",
					false);

	private final CheckboxSetting checkLOS = new CheckboxSetting(
			"Check line of sight",
			"Ensures that you don't reach through blocks when placing or left-clicking end crystals.\n\n"
					+ "Slower but can help with anti-cheat plugins.",
			false);

	private final SliderSetting tick = new SliderSetting("Tick",
			"When a desired tick is reached, only then will placing be engaged. \n" +
					"Of-course only if autoPlace is on.",
			6, 1, 20, 1, SliderSetting.ValueDisplay.DECIMAL);

	public CrystalAuraHack() {
		super("CrystalAura", "Automates crystal PvP");

		setCategory(Category.COMBAT);
		addSetting(range);
		addSetting(autoPlace);
		addSetting(faceBlocks);
		addSetting(checkLOS);
		addSetting(tick);
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
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.START) return;

		if (isEnabled()) {
			// Your CrystalAura logic here
			ArrayList<Entity> crystals = getNearbyCrystals();

			if (!crystals.isEmpty()) {
				detonate(crystals);
				return;
			}

			if (!autoPlace.isChecked()) return;

			//if (InventoryUtils.indexOf(Items.END_CRYSTAL,
			//		takeItemsFrom.getSelected().maxInvSlot) == -1) return;

			if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL)) {
				if (InventoryUtils.getSlot(Items.END_CRYSTAL) != -1) {
					if (InventoryUtils.getSlot(Items.END_CRYSTAL) > 9) {
						InventoryUtils.click(InventoryUtils.getSlot(Items.END_CRYSTAL));
						if (InventoryUtils.getSlot(Items.AIR) < 9) {
							InventoryUtils.click(InventoryUtils.getSlot(Items.AIR));
						} else if (InventoryUtils.getSlot(Items.AIR) > 9) {

						}
					} else {
						InventoryUtils.setSlot(InventoryUtils.getSlot(Items.END_CRYSTAL));
					}
				} else {
					// Empty if block
				}
			} else {
				ArrayList<Entity> targets = getNearbyTargets();
				placeCrystalsNear(targets);
			}
		}
	}

	private ArrayList<BlockPos> placeCrystalsNear(ArrayList<Entity> targets) {
		ArrayList<BlockPos> newCrystals = new ArrayList<>();

		boolean shouldSwing = false;
		for (Entity target : targets) {
			ArrayList<BlockPos> freeBlocks = getFreeBlocksNear(target);

			if (mc.player.ticksExisted % tick.getValueI() == 0) {
				for (BlockPos pos : freeBlocks)
					if (placeCrystal(pos)) {
						shouldSwing = true;
						newCrystals.add(pos);
						break;
					}
			}
		}

		if (shouldSwing) {
			// Swing the player's hand if a crystal was placed
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}

		return newCrystals;
	}

	private void detonate(ArrayList<Entity> crystals) {
		for (Entity e : crystals) {
			//faceBlocks.getSelected().face(e.getBoundingBox().getCenter());
			if (faceBlocks.isChecked()) {
				Vec3d toLook = e.getEntityBoundingBox().getCenter();
				float[] rot = RotationUtils.getNeededRotations(toLook);
				mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
			}
			mc.playerController.attackEntity(mc.player, e);
		}

		if (!crystals.isEmpty()) {
			// Swing the player's hand if crystals were detonated
			mc.player.swingArm(EnumHand.MAIN_HAND);
		}
	}

	private ArrayList<Entity> getNearbyCrystals() {
		EntityPlayer player = mc.player;
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

		Stream<Entity> stream =
				mc.world.loadedEntityList.stream()
						.filter(e -> !e.isDead)
						.filter(e -> e instanceof EntityLivingBase
								&& ((EntityLivingBase) e).getHealth() > 0)
						.filter(e -> mc.player.getDistanceSq(e) <= rangeSq);

		// Apply entity filters here if needed.

		return stream.sorted(furthestFromPlayer)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private ArrayList<BlockPos> getFreeBlocksNear(Entity target) {
		Vec3d eyesVec = RotationUtils.getEyesPos().subtract(0.5, 0.5, 0.5);
		double rangeD = range.getValue();
		double rangeSq = Math.pow(rangeD + 0.5, 2);
		int rangeI = 2;

		BlockPos center = target.getPosition();
		BlockPos min = center.add(-rangeI, -rangeI, -rangeI);
		BlockPos max = center.add(rangeI, rangeI, rangeI);
		AxisAlignedBB targetBB = target.getEntityBoundingBox();

		Vec3d targetEyesVec = target.getPositionVector().add(new Vec3d(0, target.getEyeHeight(), 0));

		Comparator<BlockPos> closestToTarget =
				Comparator.<BlockPos>comparingDouble(
						pos -> targetEyesVec.squareDistanceTo(new Vec3d(pos)));

		ArrayList<BlockPos> freeBlocks = new ArrayList<>();
		for (int x = min.getX(); x <= max.getX(); x++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					BlockPos pos = new BlockPos(x, y, z);

					// Check if the position is within range
					if (eyesVec.squareDistanceTo(new Vec3d(pos)) <= rangeSq) {
						// Check if the block is replaceable and has a crystal base
						if (isReplaceable(pos) && hasCrystalBase(pos)) {
							// Check if it doesn't intersect with the target's bounding box
							if (!targetBB.intersects(new AxisAlignedBB(pos))) {
								freeBlocks.add(pos);
							}
						}
					}
				}
			}
		}

		freeBlocks.sort(closestToTarget);
		return freeBlocks;
	}

	private boolean isReplaceable(BlockPos pos) {
		IBlockState state = mc.world.getBlockState(pos);
		return state.getBlock().isAir(state, mc.world, pos) || state.getBlock().isReplaceable(mc.world, pos);
	}

	private boolean hasCrystalBase(BlockPos pos) {
		Block block = mc.world.getBlockState(pos.down()).getBlock();
		return block == Blocks.BEDROCK || block == Blocks.OBSIDIAN;
	}

	private boolean placeCrystal(BlockPos pos) {
		Vec3d eyesPos = RotationUtils.getEyesPos();
		double rangeSq = Math.pow(range.getValue(), 2);
		Vec3d posVec = new Vec3d(pos);
		double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

		for (EnumFacing side : EnumFacing.values()) {
			BlockPos neighbor = pos.offset(side);

			// Check if neighbor can be right-clicked
			if (!isClickableNeighbor(neighbor)) continue;

			Vec3d dirVec = new Vec3d(side.getDirectionVec());
			Vec3d hitVec = posVec.add(dirVec.scale(0.5));

			// Check if hitVec is within range
			if (eyesPos.squareDistanceTo(hitVec) > rangeSq) continue;

			// Check if side is visible (facing away from player)
			if (distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec))) continue;

			if (checkLOS.isChecked() && mc.world
					.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) continue;

			if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL))
				return false;

			Vec3d toLook = new Vec3d(neighbor.getX() + 0.5, mc.player.lastTickPosY > neighbor.getY() ? neighbor.getY() + 1 : neighbor.getY(), neighbor.getZ() + 0.5);
			float[] rot = RotationUtils.getNeededRotations(toLook);
			mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));

			// Place the crystal
			mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side, hitVec, EnumHand.MAIN_HAND);
			return true;
		}

		return false;
	}

	private boolean isClickableNeighbor(BlockPos pos) {
		return mc.world.getBlockState(pos).getBlock().canCollideCheck(mc.world.getBlockState(pos), false);
	}
}