package net.wurstclient.forge.hacks.player;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.club.minnced.discord.rpc.DiscordEventHandlers;
import net.wurstclient.club.minnced.discord.rpc.DiscordRPC;
import net.wurstclient.club.minnced.discord.rpc.DiscordRichPresence;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;

import java.util.Objects;

public final class Discord extends Hack {

	public static DiscordRichPresence presence = new DiscordRichPresence();

	public static DiscordRPC rpc = DiscordRPC.INSTANCE;

	public Discord() {
		super("DiscordRPC", "Discord rich presence.");
		setCategory(Category.PLAYER);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);

		DiscordEventHandlers handlers = new DiscordEventHandlers();
		rpc.Discord_Initialize("891902442999017482", handlers, true, "");

		presence.startTimestamp = System.currentTimeMillis() / 1000L;
		presence.largeImageKey = "fallen";
		rpc.Discord_UpdatePresence(presence);

		presence.largeImageText = "Fallen Utility Mod";
		rpc.Discord_UpdatePresence(presence);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			presence.details = mc.player.getName() + " | " + Objects.requireNonNull(mc.getCurrentServerData()).serverIP;
			presence.state = mc.player.getHealth() + " / " + mc.player.getMaxHealth();
		} catch (Exception ignored) {
		}
	}
}