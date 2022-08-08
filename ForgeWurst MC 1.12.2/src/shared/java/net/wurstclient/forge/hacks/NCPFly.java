package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;

public final class NCPFly extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", "1 = normal speed", 0.1, 0.05, 20, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	public NCPFly() {
		super("NCPFly", "Fly around with packets (ncp fly).");
		setCategory(Category.MOVEMENT);
		addSetting(speed);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = mc.player;

		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = speed.getValueF();

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += speed.getValueF();
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= speed.getValueF();

		mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 999, mc.player.posZ, false));
	}
}