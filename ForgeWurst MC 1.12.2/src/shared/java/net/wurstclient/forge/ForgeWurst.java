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
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.wurstclient.forge.*;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.update.WurstUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import stevebot.commands.CommandSystem;
import stevebot.commands.StevebotCommands;
import stevebot.data.blocks.*;
import stevebot.data.items.ItemLibrary;
import stevebot.data.items.ItemLibraryImpl;
import stevebot.data.items.ItemUtils;
import stevebot.events.EventManager;
import stevebot.events.EventManagerImpl;
import stevebot.events.ModEventProducer;
import stevebot.minecraft.MinecraftAdapter;
import stevebot.minecraft.MinecraftAdapterImpl;
import stevebot.misc.Config;
import stevebot.pathfinding.PathHandler;
import stevebot.player.*;
import stevebot.rendering.Renderer;
import stevebot.rendering.RendererImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(modid = net.wurstclient.forge.ForgeWurst.MODID,
		version = net.wurstclient.forge.ForgeWurst.VERSION,
		updateJSON = "https://forge.wurstclient.net/api/v1/update.json")
public final class ForgeWurst {
	public static final String MODID = "forgewurst";
	public static final String VERSION = "0.11";

	@Instance(MODID)
	private static net.wurstclient.forge.ForgeWurst forgeWurst;

	private boolean obfuscated;

	private Path configFolder;

	private HackList hax;
	private CommandList cmds;
	private KeybindList keybinds;
	private ClickGui gui;

	private IngameHUD hud;
	private CommandProcessor cmdProcessor;
	private KeybindProcessor keybindProcessor;
	private WurstUpdater updater;


	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.SERVER)
			return;

		String mcClassName = Minecraft.class.getName().replace(".", "/");
		FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;
		obfuscated = !mcClassName.equals(remapper.unmap(mcClassName));

		configFolder =
				Minecraft.getMinecraft().mcDataDir.toPath().resolve("wurst");

		try {
			Files.createDirectories(configFolder);
		} catch (IOException e) {
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

		hud = new IngameHUD(hax, gui);
		MinecraftForge.EVENT_BUS.register(hud);

		cmdProcessor = new CommandProcessor(cmds);
		MinecraftForge.EVENT_BUS.register(cmdProcessor);

		keybindProcessor = new KeybindProcessor(hax, keybinds, cmdProcessor);
		MinecraftForge.EVENT_BUS.register(keybindProcessor);

		updater = new WurstUpdater();
		MinecraftForge.EVENT_BUS.register(updater);
	}



	private static Logger logger = LogManager.getLogger(Config.MODID);

	private static EventManager eventManager;
	private static ModEventProducer eventProducer;
	private static BlockLibrary blockLibrary;
	private static BlockProvider blockProvider;
	private static ItemLibrary itemLibrary;
	private static PlayerCamera playerCamera;
	private static PlayerMovement playerMovement;
	private static PlayerInput playerInput;
	private static PlayerInventory playerInventory;
	private static Renderer renderer;
	private static PathHandler pathHandler;




	@Mod.EventHandler
	public void preeInit(FMLPreInitializationEvent event) {
		setup();
		eventProducer.onPreInit();
	}




	private void setup() {

		// minecraft
		MinecraftAdapter.initialize(new MinecraftAdapterImpl());

		// events
		eventManager = new EventManagerImpl();
		eventProducer = new ModEventProducer(eventManager);

		// block library
		blockLibrary = new BlockLibraryImpl();
		eventManager.addListener(blockLibrary.getListener());

		// block provider
		blockProvider = new BlockProviderImpl(blockLibrary);
		eventManager.addListener(blockProvider.getBlockCache().getListenerBreakBlock());
		eventManager.addListener(blockProvider.getBlockCache().getListenerPlaceBlock());

		// block utils
		BlockUtils.initialize(blockProvider, blockLibrary);

		// item library
		itemLibrary = new ItemLibraryImpl();
		eventManager.addListener(itemLibrary.getListener());

		// item utils
		ItemUtils.initialize(itemLibrary);

		// renderer
		renderer = new RendererImpl(blockProvider);
		eventManager.addListener(renderer.getListener());

		// player camera
		playerCamera = new PlayerCameraImpl();
		eventManager.addListener(playerCamera.getListener());

		// player input
		playerInput = new PlayerInputImpl();
		eventManager.addListener(playerInput.getPlayerTickListener());
		eventManager.addListener(playerInput.getConfigChangedListener());

		// player movement
		playerMovement = new PlayerMovementImpl(playerInput, playerCamera);

		// player inventory
		playerInventory = new PlayerInventoryImpl();

		// player utils
		PlayerUtils.initialize(playerInput, playerCamera, playerMovement, playerInventory);

		// path handler
		pathHandler = new PathHandler(eventManager, renderer);

		// commands
		StevebotCommands.initialize(pathHandler);
		CommandSystem.registerCommands();
	}




	@Mod.EventHandler
	public void inittt(FMLInitializationEvent event) {
		eventProducer.onInit();
	}




	@Mod.EventHandler
	public void posttInit(FMLPostInitializationEvent event) {
		eventProducer.onPostInit();
		itemLibrary.insertBlocks(blockLibrary.getAllBlocks());
		blockLibrary.insertItems(itemLibrary.getAllItems());
	}




	/**
	 * Sends the message to the players chat (if possible) and to the logger of this mod.
	 *
	 * @param message the message to log
	 */
	public static void log(String message) {
		log(true, message);
	}




	/**
	 * Sends the message to the players chat (if possible) and to the logger of this mod only if {@code Config.isVerboseMode()} is true.
	 *
	 * @param message the message to log
	 */
	public static void logNonCritical(String message) {
		log(false, message);
	}




	/**
	 * Sends the message to the players chat (if possible) and to the logger of this mod.
	 *
	 * @param message  the message to log
	 * @param critical set to false to not send the message if {@code Config.isVerboseMode()} is false
	 */
	public static void log(boolean critical, String message) {
		if (!Config.isVerboseMode() && !critical) {
			return;
		}
		if (PlayerUtils.getPlayer() == null) {
			getLogger().info(message);
		} else {
			PlayerUtils.sendMessage(message);
		}
	}




	/**
	 * @return the {@link Logger}
	 */
	public static Logger getLogger() {
		return logger;
	}


	public static net.wurstclient.forge.ForgeWurst getForgeWurst() {
		return forgeWurst;
	}

	public boolean isObfuscated() {
		return obfuscated;
	}

	public HackList getHax() {
		return hax;
	}

	public CommandList getCmds() {
		return cmds;
	}

	public KeybindList getKeybinds() {
		return keybinds;
	}

	public ClickGui getGui() {
		return gui;
	}

	public Path getFriends() {
		return configFolder.resolve("friends.json");
	}
}