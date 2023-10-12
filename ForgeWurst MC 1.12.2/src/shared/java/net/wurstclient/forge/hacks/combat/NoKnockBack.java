package net.wurstclient.forge.hacks.combat;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class NoKnockBack extends Hack {

	public NoKnockBack() {
		super("NoKnockBack", "Prevents knockback.");
		setCategory(Category.COMBAT);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (event.getPacket() instanceof SPacketEntityVelocity || event.getPacket() instanceof SPacketExplosion) {
			event.setCanceled(true);
		}
	}
}