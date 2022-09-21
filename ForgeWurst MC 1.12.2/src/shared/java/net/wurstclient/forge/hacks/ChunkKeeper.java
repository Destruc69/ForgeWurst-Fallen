/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class ChunkKeeper extends Hack {
	public ChunkKeeper() {
		super("ChunkKeeper", "Keeps chunks you rendered to stay rendered.");
		setCategory(Category.RENDER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketUnloadChunk) {
				event.setCanceled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (event.getPacket() instanceof SPacketUnloadChunk) {
				event.setCanceled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}