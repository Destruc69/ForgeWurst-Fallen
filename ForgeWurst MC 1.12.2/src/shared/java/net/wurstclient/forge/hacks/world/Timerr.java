/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.settings.SliderSetting.ValueDisplay;

import java.lang.reflect.Field;

public final class Timerr extends Hack {
	private final SliderSetting timerSpeed =
			new SliderSetting("TimerSpeed", 0.9, 0.1, 20, 0.1, ValueDisplay.DECIMAL);

	public Timerr() {
		super("Timer", "Changes the speed of almost everything.");
		setCategory(Category.WORLD);
		addSetting(timerSpeed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		setTickLength(50.0F);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		setTickLength(50.0F / timerSpeed.getValueF());
	}

	private void setTickLength(float tickLength)
	{
		try
		{
			Field fTimer = mc.getClass().getDeclaredField(
					wurst.isObfuscated() ? "field_71428_T" : "timer");
			fTimer.setAccessible(true);

			if(WMinecraft.VERSION.equals("1.10.2"))
			{
				Field fTimerSpeed = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_74278_d" : "timerSpeed");
				fTimerSpeed.setAccessible(true);
				fTimerSpeed.setFloat(fTimer.get(mc), 50 / tickLength);

			}else
			{
				Field fTickLength = Timer.class.getDeclaredField(
						wurst.isObfuscated() ? "field_194149_e" : "tickLength");
				fTickLength.setAccessible(true);
				fTickLength.setFloat(fTimer.get(mc), tickLength);
			}

		}catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMainMenu) {
			GuiMainMenu mainMenu = (GuiMainMenu) event.getGui();

			// You may need to adjust these coordinates and dimensions based on your needs
			int backgroundX = 0;
			int backgroundY = 0;
			int backgroundWidth = mainMenu.width;
			int backgroundHeight = mainMenu.height;

			mainMenu.drawModalRectWithCustomSizedTexture(
					backgroundX, backgroundY, 0, 0, backgroundWidth, backgroundHeight, backgroundWidth, backgroundHeight);

			// Draw the new background image
			mainMenu.mc.getTextureManager().bindTexture(new ResourceLocation(ForgeWurst.MODID, "background.png"));
			mainMenu.drawTexturedModalRect(
					backgroundX, backgroundY, 0, 0, backgroundWidth, backgroundHeight);

			// Cancel the default rendering to avoid overlapping backgrounds
			event.setCanceled(true);
		}
	}
}