package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.*;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class NCPFly extends Hack {

	private final SliderSetting teleportAmount =
			new SliderSetting("TeleportAmount", "How high we teleport up", 1337, 500, 5000, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting posPacketStrength =
			new SliderSetting("How strong are the pos packets?", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting telePacketStrength =
			new SliderSetting("How strong are the confirm tele packets?", 2, 1, 10, 1, SliderSetting.ValueDisplay.DECIMAL);

	public NCPFly() {
		super("NCPFly", "Fly around with packets (ncp fly).");
		setCategory(Category.MOVEMENT);
		addSetting(teleportAmount);
		addSetting(posPacketStrength);
		addSetting(telePacketStrength);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			assert event != null;
			mc.player.setVelocity(0, 0, 0);

			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = 0.05f;

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += 0.05;
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= 0.05;

			for (int x = 0; x < posPacketStrength.getValueF(); x++) {
				mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + mc.player.motionY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));

				double y = mc.player.posY + mc.player.motionY + teleportAmount.getValueF();
				mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, y, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		assert event != null;
		try {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
				for (int x = 0; x < telePacketStrength.getValueF(); x++) {
					mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
				}
				for (int x = 0; x < posPacketStrength.getValueF(); x++) {
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
				}
				mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());

				event.setCanceled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		assert event != null;
		try {
			if (event.getPacket() instanceof SPacketPlayerPosLook) {
				SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
				for (int x = 0; x < telePacketStrength.getValueF(); x++) {
					mc.player.connection.sendPacket(new CPacketConfirmTeleport(packet.getTeleportId()));
				}
				for (int x = 0; x < posPacketStrength.getValueF(); x++) {
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
				}
				mc.player.setPosition(packet.getX(), packet.getY(), packet.getZ());

				event.setCanceled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}