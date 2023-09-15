package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
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
import net.wurstclient.forge.utils.RotationUtils;

public final class Arson extends Hack {

	private static final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.LEGIT);

	private enum Mode {
		PACKET("Packet"),
		LEGIT("Legit");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public Arson() {
		super("Arson", "Lights players on fire with a flint n' steel.");
		setCategory(Category.COMBAT);
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
		Entity entity = findClosestEntity();

		if (entity != null) {
			if (mc.player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL) {
				BlockPos posToLight = getPosToLight(entity);
				if (posToLight != null && mc.world.getBlockState(posToLight).getBlock() != Blocks.FIRE) {
					mc.playerController.processRightClickBlock(mc.player, mc.world, posToLight, EnumFacing.UP, new Vec3d(0.5, 0, 0.5), EnumHand.MAIN_HAND);
					mc.player.swingArm(EnumHand.MAIN_HAND);

					float[] rot = RotationUtils.getNeededRotations(new Vec3d(posToLight.getX() + 0.5, posToLight.getY(), posToLight.getZ() + 0.5));
					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rot[0], rot[1], mc.player.onGround));
				}
			} else {
				int slot = getSlot(Items.FLINT_AND_STEEL);
				if (slot != -1) {
					mc.player.inventory.currentItem = slot;
					mc.playerController.updateController();
				}
			}
		}
	}

	private EntityLivingBase findClosestEntity() {
		EntityLivingBase closestEntity = null;
		double closestDistance = Double.MAX_VALUE;

		for (Entity entity : mc.world.loadedEntityList) {
			if (entity instanceof EntityLivingBase && entity != mc.player) {
				double distance = mc.player.getDistanceSq(entity);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEntity = (EntityLivingBase) entity;
				}
			}
		}
		assert closestEntity != null;
		if (mc.player.getDistance(closestEntity) < 5) {
			return closestEntity;
		} else {
			return null;
		}
	}

	private int getSlot(Item item) {
		for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
			if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
				return i;
			}
		}
		return -1;
	}

	private BlockPos getPosToLight(Entity entity) {
		BlockPos blockPos = null;
		BlockPos ePos = entity.getPosition();

		if (!mc.world.getBlockState(ePos.add(1, -1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(1, 0, 0)).getBlock().equals(Blocks.AIR)) {
			blockPos = ePos.add(1, -1, 0);
		} else if (!mc.world.getBlockState(ePos.add(-1, -1, 0)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(-1, 0, 0)).getBlock().equals(Blocks.AIR)) {
			blockPos = ePos.add(-1, -1, 0);
		} else if (!mc.world.getBlockState(ePos.add(0, -1, 1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(0, 0, 1)).getBlock().equals(Blocks.AIR)) {
			blockPos = ePos.add(0, -1, 1);
		} else if (!mc.world.getBlockState(ePos.add(0, -1, -1)).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(ePos.add(0, 0, -1)).getBlock().equals(Blocks.AIR)) {
			blockPos = ePos.add(0, -1, -1);
		}

		return blockPos;
	}
}