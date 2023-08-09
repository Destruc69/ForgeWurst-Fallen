package net.wurstclient.forge.hacks.player;

import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.movement.AutoSprintHack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class PacketLimiter extends Hack {

	private int packetCounter;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.WARN);

	public static SliderSetting maxPacketsPS =
			new SliderSetting("MaxPacketsPS", "Whats the max amount of packets per second?", 300, 100, 5000, 1, SliderSetting.ValueDisplay.INTEGER);

	public PacketLimiter() {
		super("PacketLimiter", "Limits packets.");
		setCategory(Category.PLAYER);
		addSetting(maxPacketsPS);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		packetCounter = 0;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	private void onUpdate(WUpdateEvent event) {
		if (mc.player.ticksExisted % 20 == 0) {
			packetCounter = 0;
		}
	}

	@SubscribeEvent
	private void onPacketOut(WPacketOutputEvent event) {
		if (!(event.getPacket() instanceof CPacketPlayer)) {
			if (packetCounter < maxPacketsPS.getValue()) {
				packetCounter = packetCounter + 1;
			} else {
				if (mode.getSelected().warn) {
					 mc.ingameGUI.setOverlayMessage("You are exceeding the packet limit. [" + packetCounter + "]", true);
				} else if (mode.getSelected().cancel) {
					if (!(event.getPacket() instanceof CPacketKeepAlive)) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private enum Mode {
		WARN("Warn", true, false),
		CANCEL("Cancel", false, true);

		private final String name;
		private final boolean warn;
		private final boolean cancel;

		private Mode(String name, boolean warn, boolean cancel) {
			this.name = name;
			this.warn = warn;
			this.cancel = cancel;
		}

		public String toString() {
			return name;
		}
	}
}