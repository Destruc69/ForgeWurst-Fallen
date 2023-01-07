/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public final class GodMode extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.AAC);

	public GodMode() {
		super("GodMode", "Prevents damage.");
		setCategory(Category.PLAYER);
		addSetting(mode);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + mode.getSelected().name() + "]";
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
	public void onUpdate(WPacketInputEvent event) {
		if (mode.getSelected().aac) {
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.1, mc.player.posZ, true));
		} else if (mode.getSelected().portal) {
			if (event.getPacket() instanceof CPacketConfirmTeleport) {
				event.setCanceled(true);
			}
		}
	}

	private enum Mode {
		PORTAL("Portal", true, false),
		AAC("AAC", false, true);

		private final String name;
		private final boolean portal;
		private final boolean aac;

		private Mode(String name, boolean portal, boolean aac) {
			this.name = name;
			this.portal = portal;
			this.aac = aac;
		}

		public String toString() {
			return name;
		}
	}
}