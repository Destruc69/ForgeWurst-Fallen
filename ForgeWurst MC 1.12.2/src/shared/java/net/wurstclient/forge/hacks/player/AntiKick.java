package net.wurstclient.forge.hacks.player;

import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.Setting;

public final class AntiKick extends Hack {
	public AntiKick() {
		super("AntiKick", "Prevents kick exploits.");
		setCategory(Category.PLAYER);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (event.getPacket() instanceof SPacketAdvancementInfo)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketParticles)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketMaps)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketSpawnPainting)
			event.setCanceled(true);
		if (event.getPacket() instanceof SPacketSpawnObject)
			event.setCanceled(true);
	}
}