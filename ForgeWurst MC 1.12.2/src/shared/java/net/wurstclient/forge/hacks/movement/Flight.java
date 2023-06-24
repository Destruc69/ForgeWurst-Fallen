package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketSpectate;
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
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;
import net.wurstclient.forge.utils.MathUtils;

public final class Flight extends Hack
{
	private int slot = 0;
	private int startY;
	private boolean canFly;

	private enum Mode {
		AAC("AAC", true, false, false, false, false, false),
		CUBECRAFT("CubeCraft", false, true, false, false, false, false),
		HYPIXEL("Hypixel", false, false, true, false, false, false),
		MINEPLEX("Mineplex", false, false, false, true, false, false),
		VANILLA("Vanilla", false, false, false, false, true, false),
		NCP("NCP", false, false, false, false, false, true);

		private final String name;
		private final boolean aac;
		private final boolean cubecraft;
		private final boolean hypixel;
		private final boolean mineplex;
		private final boolean vanilla;
		private final boolean ncp;

		private Mode(String name, boolean aac, boolean cubecraft, boolean hypixel, boolean mineplex, boolean vanilla, boolean ncp) {
			this.name = name;
			this.aac = aac;
			this.cubecraft = cubecraft;
			this.hypixel = hypixel;
			this.mineplex = mineplex;
			this.vanilla = vanilla;
			this.ncp = ncp;
		}

		public String toString() {
			return name;
		}
	}

	private final SliderSetting speed =
			new SliderSetting("Speed", 1, 0.05, 5, 0.05, ValueDisplay.DECIMAL);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.HYPIXEL);


	public Flight()
	{
		super("Flight",
				"Allows you to fly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(speed);
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
		slot = 0;
	}

	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().vanilla) {
			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = speed.getValueF();

			if(mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValue();
			if(mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValue();
		} else if (mode.getSelected().hypixel) {
			mc.player.motionY = 0.0D;
			mc.player.onGround = true;

			for (int i = 0; i < 3; ++i) {
				mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-12D, mc.player.posZ);
				if (mc.player.ticksExisted % 3 == 0) {
					mc.player.setPosition(mc.player.posX, mc.player.posY - 1.0E-12D, mc.player.posZ);
				}
			}
		} else if (mode.getSelected().cubecraft) {
			mc.player.motionY = 0.0D;
			mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-9D, mc.player.posZ);
			double v1 = 1.0D;
			float v3 = mc.player.moveForward;
			float v4 = mc.player.moveStrafing;
			float v5 = mc.player.rotationYaw;
			if (v3 != 0.0F) {
				if (v4 >= 1.0F) {
					v5 += (float) (v3 > 0.0F ? -45 : 45);
					v4 = 0.0F;
				} else if (v4 <= -1.0F) {
					v5 += (float) (v3 > 0.0F ? 45 : -45);
					v4 = 0.0F;
				}

				if (v3 > 0.0F) {
					v3 = 1.0F;
				} else if (v3 < 0.0F) {
					v3 = -1.0F;
				}
			}

			double v6 = Math.cos(Math.toRadians((double) (v5 + 90.0F)));
			double v8 = Math.sin(Math.toRadians((double) (v5 + 90.0F)));
			mc.player.motionX = (double) v3 * v1 * v6 + (double) v4 * v1 * v8;
			mc.player.motionZ = (double) v3 * v1 * v8 - (double) v4 * v1 * v6;
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY = 0.5D * v1;
			}

			if (mc.player.isSneaking()) {
				mc.player.motionY = 0.5D * -v1;
			}

		} else if (mode.getSelected().mineplex) {
			if (this.slot >= 0 && this.slot <= 8) {
				mc.player.inventory.currentItem = this.slot;
				mc.player.getHeldItem(EnumHand.MAIN_HAND);
				++this.slot;
			}

			mc.playerController.processRightClickBlock(mc.player, mc.world, new BlockPos(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ), EnumFacing.DOWN, new Vec3d(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ), EnumHand.MAIN_HAND);
			if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {
				double[] spd = MathUtils.directionSpeed(0.256D);
				mc.player.motionX = spd[0];
				mc.player.motionZ = spd[1];
			}

			mc.player.motionY = 0.0D;
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.player.motionY = 0.0D;
			}

			mc.player.onGround = true;
		} else if (mode.getSelected().aac) {
			if (mc.player.fallDistance >= 4.0F && !canFly) {
				startY = (int) mc.player.posY;
				mc.player.motionY = 0.1D;
			}

			if (mc.player.onGround || mc.player.isInWater()) {
				this.canFly = false;
			}

			if (this.canFly) {
				double[] spd = MathUtils.directionSpeed(0.25D);
				mc.player.motionX = spd[0];
				mc.player.motionZ = spd[1];
				if (mc.player.posY <= startY) {
					mc.player.motionY = 0.8D;
				}
			}
		 } else if (mode.getSelected().ncp) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.28, mc.player.posZ, true));
			} else {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.28, mc.player.posZ, false));
			}
			mc.player.connection.sendPacket(new CPacketSpectate(mc.player.getUniqueID()));
			mc.player.connection.sendPacket(new CPacketInput(0, 0, true, true));
			mc.player.motionY = 0;
		}
	}
}