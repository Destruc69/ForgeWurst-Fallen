/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

public final class Animations extends Hack {

	private static final Minecraft mc = Minecraft.getMinecraft();

	private static final SliderSetting xv =
			new SliderSetting("X", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting yv =
			new SliderSetting("Y", 0, -2, 2, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private static final SliderSetting zv =
			new SliderSetting("Z", 0, -2, 2, 1, SliderSetting.ValueDisplay.DECIMAL);

	public Animations() {
		super("Animations", "Animations, explains its self.");
		setCategory(Category.PLAYER);
		addSetting(xv);
		addSetting(yv);
		addSetting(zv);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		xv.setValue(0);
		yv.setValue(0);
		zv.setValue(0);
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		ItemRenderer ir = mc.getItemRenderer();
		GlStateManager.translate(xv.getValueI(), yv.getValueF(), zv.getValueI());
		ir.updateEquippedItem();
	}
}