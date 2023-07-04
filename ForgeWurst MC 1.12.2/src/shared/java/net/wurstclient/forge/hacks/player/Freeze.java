package net.wurstclient.forge.hacks.player;

import net.minecraft.network.Packet;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

import java.util.ArrayList;

public final class Freeze extends Hack {

	private ArrayList<Packet> packets;

	private final CheckboxSetting inputPackets =
			new CheckboxSetting("InputPackets", "Input packets instead of output packets. \n" +
					"This works differently, we dont send the packets all at once but when you re-enable \n" +
					"the module everything i will suddenly start moving and teleporting",
					false);

	public Freeze() {
		super("Freeze", "Freeze packets and send it all at once, once the module is disabled.");
		setCategory(Category.PLAYER);
		addSetting(inputPackets);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		packets = new ArrayList<>();
	}

	protected void onDisable() {
		if (!inputPackets.isChecked()) {
			MinecraftForge.EVENT_BUS.unregister(this);
			for (Packet packet : packets) {
				mc.player.connection.sendPacket(packet);
			}
			packets.clear();
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (!inputPackets.isChecked()) {
			packets.add(event.getPacket());
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		if (inputPackets.isChecked()) {
			event.setCanceled(true);
		}
	}
}
