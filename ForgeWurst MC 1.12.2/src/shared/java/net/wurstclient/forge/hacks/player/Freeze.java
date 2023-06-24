package net.wurstclient.forge.hacks.player;

import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

import java.util.ArrayList;

public final class Freeze extends Hack {

	private ArrayList<Packet> packets;

	public Freeze() {
		super("Freeze", "Freeze packets and send it all at once, once the module is disabled.");
		setCategory(Category.PLAYER);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		packets = new ArrayList<>();
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		for (Packet packet : packets) {
			mc.player.connection.sendPacket(packet);
		}
		packets.clear();
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		packets.add(event.getPacket());
		event.setCanceled(true);
	}
}