package net.wurstclient.forge.hacks.player;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

import java.lang.reflect.Field;

public final class AntiForge extends Hack {
	public AntiForge() {
		super("AntiForge", "Tricks the server into thinking your using a vanilla client.");
		setCategory(Category.PLAYER);
	}

	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketSend(WPacketOutputEvent event) {
		if (!mc.isIntegratedServerRunning()) {
			if (event.getPacket() instanceof FMLProxyPacket) {
				event.setCanceled(true);
			} else if (event.getPacket() instanceof CPacketCustomPayload) {
				CPacketCustomPayload customPayload = (CPacketCustomPayload) event.getPacket();
				if (customPayload.getChannelName().equals("MC|Brand")) {
					try {
						Field dataField = CPacketCustomPayload.class.getDeclaredField("data");
						dataField.setAccessible(true);
						ByteBuf newData = Unpooled.buffer();
						newData.writeBytes("vanilla".getBytes());
						dataField.set(customPayload, newData);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}