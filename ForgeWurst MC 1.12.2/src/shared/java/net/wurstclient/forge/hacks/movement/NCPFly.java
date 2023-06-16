package net.wurstclient.forge.hacks.movement;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.MathUtils;

public final class NCPFly extends Hack {

	private final SliderSetting speed =
			new SliderSetting("Speed", "The speed of the speed (lol)", 0.2, 0.1, 5, 0.01, SliderSetting.ValueDisplay.DECIMAL);

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

	double[] a = MathUtils.directionSpeed(speed.getValueF());
	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			mc.player.setPosition(mc.player.lastTickPosX + mc.player.motionX + a[0], mc.player.prevPosY, mc.player.lastTickPosZ + mc.player.motionZ + a[1]);
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof CPacketPlayer.Position) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.lastTickPosX + mc.player.motionX + a[0], mc.player.prevPosY, mc.player.lastTickPosZ + mc.player.motionZ + a[1], true));
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
			mc.player.connection.sendPacket(new CPacketConfirmTeleport(sPacketPlayerPosLook.getTeleportId()));
		} catch (Exception ignored) {
		}
	}
}