/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.block.BlockPortal;
import net.minecraft.client.gui.advancements.GuiAdvancement;
import net.minecraft.client.gui.advancements.GuiAdvancementTab;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.fml.client.GuiDupesFound;
import net.minecraftforge.fml.client.GuiNotification;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.StartupQuery;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;

import java.lang.reflect.Field;

public final class NoRender extends Hack {

	private final CheckboxSetting portalGui =
			new CheckboxSetting("PortalGUI", "Allows you to open GUIs while in a portal",
					false);

	public NoRender() {
		super("NoRender", "Cancels block overlaying events.");
		setCategory(Category.RENDER);
		addSetting(portalGui);
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
	public void onRender(RenderGameOverlayEvent event) {

	}

	@SubscribeEvent
	public void onRenderOverlay(RenderBlockOverlayEvent event) {
		event.setCanceled(true);
	}
}