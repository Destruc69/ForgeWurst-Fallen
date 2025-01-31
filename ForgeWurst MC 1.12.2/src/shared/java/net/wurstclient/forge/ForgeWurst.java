/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.other.AutoJoin;
import net.wurstclient.forge.other.GUITweaks;
import net.wurstclient.forge.other.customs.notifications.NotificationManager;
import net.wurstclient.forge.update.WurstUpdater;
import net.wurstclient.forge.waypoints.Waypoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@Mod(modid = ForgeWurst.MODID,
		version = ForgeWurst.VERSION,
		updateJSON = "https://forge.wurstclient.net/api/v1/update.json")
public final class ForgeWurst
{
	public static final String MODID = "forgewurst";
	public static final String VERSION = "0.11";

	@Instance(MODID)
	private static ForgeWurst forgeWurst;

	private boolean obfuscated;

	private Path configFolder;

	private HackList hax;
	private CommandList cmds;
	private KeybindList keybinds;
	private ClickGui gui;
	private FriendsList friends;
	private NotePad notePad;
	private Waypoints waypoints;

	private GUITweaks guiTweaks;
	private AutoJoin autoJoin;

	private NotificationManager notificationManager;

	private IngameHUD hud;
	private CommandProcessor cmdProcessor;
	private KeybindProcessor keybindProcessor;
	private WurstUpdater updater;

	@EventHandler
	public void init(FMLInitializationEvent event) throws Exception {
		if(event.getSide() == Side.SERVER)
			return;

		String mcClassName = Minecraft.class.getName().replace(".", "/");
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		obfuscated = !mcClassName.equals(remapper.unmap(mcClassName));

		configFolder =
				Minecraft.getMinecraft().mcDataDir.toPath().resolve("wurst");
		try
		{
			Files.createDirectories(configFolder);
		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}

		hax = new HackList(configFolder.resolve("enabled-hacks.json"),
				configFolder.resolve("settings.json"));
		hax.loadEnabledHacks();
		hax.loadSettings();

		cmds = new CommandList();

		keybinds = new KeybindList(configFolder.resolve("keybinds.json"));
		keybinds.init();

		gui = new ClickGui(configFolder.resolve("windows.json"));
		gui.init(hax);

		friends = new FriendsList(configFolder.resolve("friends.json"));
		friends.init();

		notePad = new NotePad(configFolder.resolve("notes.json"));
		notePad.init();

		waypoints = new Waypoints(configFolder.resolve("waypoints.json"));
		waypoints.init();

		hud = new IngameHUD(hax, gui);
		MinecraftForge.EVENT_BUS.register(hud);

		cmdProcessor = new CommandProcessor(cmds);
		MinecraftForge.EVENT_BUS.register(cmdProcessor);

		keybindProcessor = new KeybindProcessor(hax, keybinds, cmdProcessor);
		MinecraftForge.EVENT_BUS.register(keybindProcessor);

		updater = new WurstUpdater();
		MinecraftForge.EVENT_BUS.register(updater);

		guiTweaks = new GUITweaks();
		MinecraftForge.EVENT_BUS.register(guiTweaks);

		autoJoin = new AutoJoin();
		MinecraftForge.EVENT_BUS.register(autoJoin);

		notificationManager = new NotificationManager();
		NotificationManager.notificationArrayList = new ArrayList<>();
		MinecraftForge.EVENT_BUS.register(notificationManager);
	}

	public static ForgeWurst getForgeWurst()
	{
		return forgeWurst;
	}

	public boolean isObfuscated()
	{
		return obfuscated;
	}

	public HackList getHax()
	{
		return hax;
	}

	public CommandList getCmds()
	{
		return cmds;
	}

	public KeybindList getKeybinds()
	{
		return keybinds;
	}

	public ClickGui getGui()
	{
		return gui;
	}

	public FriendsList getFriendsList() {
		return friends;
	}

	public NotePad getNotePad(){return notePad;}

	public Waypoints getWaypoints(){return waypoints;}
}