/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.pathing;

import net.minecraftforge.common.MinecraftForge;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;

public final class PathfinderModule extends Hack {

	public static CheckboxSetting debug =
			new CheckboxSetting("Debug", "Debug mode.",
					false);

	private static final EnumSetting<Mode> renderMode =
			new EnumSetting<>("RenderMode", Mode.values(), Mode.BARITONE);

	public static SliderSetting pathRed = new SliderSetting("Path red",
			"Path red", 0, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting pathGreen = new SliderSetting("Path green",
			"Path green", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting pathBlue = new SliderSetting("Path blue",
			"Path blue", 0, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);
	public static SliderSetting lineWidth = new SliderSetting("LineWidth",
			"The width of the lines for the renders", 1, 0.1, 10, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final EnumSetting<ModeType> modeType =
			new EnumSetting<>("ModeType", ModeType.values(), ModeType.AUTO);

	public static SliderSetting depth = new SliderSetting("Depth",
			"How deep should the pathfinding algorithm more? Only increase if your computer can handle it.", 4, 2, 20, 1, SliderSetting.ValueDisplay.DECIMAL);

	public static SliderSetting loops = new SliderSetting("Loops",
			"How many dynamically calculated paths should the pathfinding algorithm look at? Only increase if your computer can handle it.", 1000, 1000, 25000, 1000, SliderSetting.ValueDisplay.DECIMAL);

	public PathfinderModule() {
		super("Pathfinder", "Pathfinding settings.");
		setCategory(Category.PATHING);
		addSetting(debug);
		addSetting(renderMode);
		addSetting(pathRed);
		addSetting(pathGreen);
		addSetting(pathBlue);
		addSetting(lineWidth);
		addSetting(modeType);
		addSetting(depth);
		addSetting(loops);
	}

	private enum Mode {
		BARITONE("Baritone", true, false, false),
		TESLA("Tesla", false, true, false),
		BLOCK("Block", false, false, true);

		private final String name;
		private final boolean baritone;
		private final boolean tesla;
		private final boolean block;

		Mode(String name, boolean baritone, boolean tesla, boolean block) {
			this.name = name;
			this.tesla = tesla;
			this.baritone = baritone;
			this.block = block;
		}

		public String toString() {
			return name;
		}
	}

	private enum ModeType {
		AUTO("Auto", true, false),
		RENDER("Render", false, true);

		private final String name;
		private final boolean auto;
		private final boolean render;

		ModeType(String name, boolean auto, boolean render) {
			this.name = name;
			this.auto = auto;
			this.render = render;
		}

		public String toString() {
			return name;
		}
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		this.setEnabled(false);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	public static boolean isRenderTesla() {
		return renderMode.getSelected().tesla;
	}
	public static boolean isRenderBaritone() {
		return renderMode.getSelected().baritone;
	}
	public static boolean isRenderBlock() {
		return renderMode.getSelected().block;
	}
	public static boolean isAuto() {
		return modeType.getSelected().auto;
	}
	public static boolean isRender() {
		return modeType.getSelected().render;
	}
}