package net.wurstclient.forge.hacks.combat;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

public final class NoKnockBack extends Hack {
	private final CheckboxSetting bypass =
			new CheckboxSetting("Bypass",
					false);

	public NoKnockBack() {
		super("NoKnockBack", "Prevents knockback.");
		setCategory(Category.COMBAT);
		addSetting(bypass);
	}


	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (bypass.isChecked()) {
			if (mc.player.hurtTime > 0) {
				mc.player.motionX = mc.player.motionX / 2;
				mc.player.motionZ = mc.player.motionZ / 2;
			}
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (!bypass.isChecked()) {
				if (event.getPacket() instanceof SPacketEntityVelocity || event.getPacket() instanceof SPacketExplosion) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}