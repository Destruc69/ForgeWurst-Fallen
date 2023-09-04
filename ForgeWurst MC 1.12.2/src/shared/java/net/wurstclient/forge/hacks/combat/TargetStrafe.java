package net.wurstclient.forge.hacks.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.RotationUtils;

public final class TargetStrafe extends Hack {

	public TargetStrafe() {
		super("TargetStrafe", "Whoosh around entities like flash.");
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
			double distance = mc.player.getDistance(entity);
			double radius = 3.0; // Set the radius of your circle here

			if (distance < radius) {
				double angle = Math.atan2(mc.player.posZ - entity.posZ, mc.player.posX - entity.posX);
				double offsetX = radius * Math.cos(angle);
				double offsetZ = radius * Math.sin(angle);

				mc.player.setPosition(entity.posX + offsetX, mc.player.lastTickPosY, entity.posZ + offsetZ);

				float[] rot = RotationUtils.getNeededRotations(new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));
				mc.player.rotationYaw = rot[0];
			}
		}
	}

	private EntityLivingBase findClosestEntity() {
		EntityLivingBase closestEntity = null;
		double closestDistance = Double.MAX_VALUE;

		for (Entity entity : mc.player.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityLivingBase && entity != mc.player) {
				double distance = mc.player.getDistanceSq(entity.posX, entity.posY, entity.posZ);
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
}