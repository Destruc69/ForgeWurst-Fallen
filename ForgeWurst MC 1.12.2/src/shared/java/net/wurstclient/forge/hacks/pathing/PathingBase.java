/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WChatInputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.TextUtil;
import stevebot.misc.Config;
import stevebot.pathfinding.path.PathRenderable;

public final class PathingBase extends Hack {

	private final CheckboxSetting serverBypass =
			new CheckboxSetting("ServerBypass", "Allows the command to work on servers that block it",
					false);

	private final CheckboxSetting help =
			new CheckboxSetting("Help (what is this?)", TextUtil.coloredString("STEVE-BOT", TextUtil.Color.AQUA) + "\n" +
					TextUtil.coloredString("Steve-Bot, Created By SMILEY4", TextUtil.Color.GOLD) + " " + "is an A* Pathfinding bot. \n" +
					"Usage: \n" +
					"> /pathTo <x> <y> <z> \n" +
					"> /pathLevel <y> \n" +
					"> /pathBlock <block> \n" +
					TextUtil.coloredString("This is just what some of the bot can do, Please use /help to find more.", TextUtil.Color.RED),
					false);

	private final CheckboxSetting keepPathRender =
			new CheckboxSetting("KeepPathRender", "Should we always render the path?",
					false);

	private final SliderSetting slowDownPath =
			new SliderSetting("SlowDownPath", "Slow down value for pathing \n" +
					"Default: -1", -0.4, -2, 0, -0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting pathTimeout =
			new SliderSetting("PathTimeout", "How much time can we take when thinking? \n" +
					"Default: 10", 10, 0, 20, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting verbose =
			new CheckboxSetting("Verbose", "Should we show the verbose?",
					false);

	private final CheckboxSetting chunkCache =
			new CheckboxSetting("ChunkCache", "Should we show the chunk cache?",
					false);

	private final CheckboxSetting nodeCache =
			new CheckboxSetting("NodeCache", "Should we show the node cache?",
					false);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode \n" +
					"Default: ACTIONTYPE", Mode.values(), Mode.ACTIONTYPE);


	public PathingBase() {
		super("PathingBase", "Options for the pathfinder.");
		setCategory(Category.PATHING);
		addSetting(serverBypass);
		addSetting(help);
		addSetting(keepPathRender);
		addSetting(slowDownPath);
		addSetting(pathTimeout);
		addSetting(verbose);
		addSetting(chunkCache);
		addSetting(nodeCache);
		addSetting(mode);
	}

	private enum Mode {
		ACTIONTYPE("ACTION-TYPE", true, false, false, false, false),
		PATHID("PATH-ID", false, true, false, false, false),
		ACTIONCOST("ACTION-COST", false, false, true, false, false),
		ACTIONID("ACTION-ID", false, false, false, true, false),
		SOLID("SOLID", false, false, false, false, true);


		private final String name;
		private final boolean actiontype;
		private final boolean pathid;
		private final boolean actioncost;
		private final boolean actionid;
		private final boolean solid;

		private Mode(String name, boolean actiontype, boolean pathid, boolean actioncost, boolean actionid, boolean solid) {
			this.name = name;
			this.actiontype = actiontype;
			this.pathid = pathid;
			this.actioncost = actioncost;
			this.actionid = actionid;
			this.solid = solid;
		}

		public String toString() {
			return name;
		}
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
	public void onUpdate(WUpdateEvent event) {
		if (verbose.isChecked()) {
			if (!Config.isVerboseMode()) {
				Config.setVerboseMode(true);
			}
		} else {
			if (Config.isVerboseMode()) {
				Config.setVerboseMode(false);
			}
		}
		if (chunkCache.isChecked()) {
			if (!Config.isShowChunkCache()) {
				Config.setShowChunkCache(true);
			}
		} else {
			if (Config.isShowChunkCache()) {
				Config.setShowChunkCache(false);
			}
		}
		if (nodeCache.isChecked()) {
			if (!Config.isShowNodeCache()) {
				Config.setShowNodeCache(true);
			}
		} else {
			if (Config.isShowNodeCache()) {
				Config.setShowNodeCache(false);
			}
		}
		if (keepPathRender.isChecked()) {
			if (!Config.isKeepPathRenderable()) {
				Config.setKeepPathRenderable(true);
			}
		} else {
			if (Config.isKeepPathRenderable()) {
				Config.setKeepPathRenderable(false);
			}
		}
		if (Config.getPathfindingSlowdown() != slowDownPath.getValueF()) {
			Config.setPathfindingSlowdown(slowDownPath.getValueI());
		}
		if (Config.getPathfindingTimeout() != pathTimeout.getValueF()) {
			Config.setPathfindingTimeout(pathTimeout.getValueF());
		}

		if (mode.getSelected().solid) {
			if (!Config.getPathStyle().equals(PathRenderable.PathStyle.SOLID)) {
				Config.setPathStyle(PathRenderable.PathStyle.SOLID);
			}
		}
		if (mode.getSelected().actionid) {
			if (!Config.getPathStyle().equals(PathRenderable.PathStyle.ACTION_ID)) {
				Config.setPathStyle(PathRenderable.PathStyle.ACTION_ID);
			}
		}
		if (mode.getSelected().actioncost) {
			if (!Config.getPathStyle().equals(PathRenderable.PathStyle.ACTION_COST)) {
				Config.setPathStyle(PathRenderable.PathStyle.ACTION_COST);
			}
		}
		if (mode.getSelected().pathid) {
			if (!Config.getPathStyle().equals(PathRenderable.PathStyle.PATH_ID)) {
				Config.setPathStyle(PathRenderable.PathStyle.PATH_ID);
			}
		}
		if (mode.getSelected().actiontype) {
			if (!Config.getPathStyle().equals(PathRenderable.PathStyle.ACTION_TYPE)) {
				Config.setPathStyle(PathRenderable.PathStyle.ACTION_TYPE);
			}
		}

		if (help.isChecked()) {
			serverBypass.setChecked(false);
			help.setChecked(false);
			ChatUtils.message(TextUtil.coloredString("STEVE-BOT BY SMILEY4", TextUtil.Color.AQUA) + " " + "\n" +
					"> /path (to) <x> <y> <z> \n" +
					"> /path (from) <x> <y> <z> (to) <x> <y> <z> \n" +
					"> /path <x> <y> <z> freelook \n" +
					"> /path <distance> \n" +
					"> /path <distance> freelook \n" +
					"> /path <y level> \n" +
					"> /path <y level> freelook \n" +
					"> /path <block> \n" +
					"> /path <block> freelook \n" +
					"> /freelook \n" +
					"> /follow stop \n" +
					"> /statistics \n" +
					"> /statisticsconsole \n" +
					"> /actioncoststats \n" +
					"> /clearblockcache \n" +
					TextUtil.coloredString("Useful commands:", TextUtil.Color.GOLD) + "\n" +
					"> /path portal \n");
		}
	}

	@SubscribeEvent
	public void onComSend(WChatInputEvent event) {
		try {
			if (mc.world != null && mc.player != null) {
				if (serverBypass.isChecked())
					event.setCanceled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}