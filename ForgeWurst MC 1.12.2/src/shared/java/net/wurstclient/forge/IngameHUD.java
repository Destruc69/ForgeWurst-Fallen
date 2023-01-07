/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.clickgui.ClickGuiScreen;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.ClickGuiHack;
import net.wurstclient.forge.hudmodules.HudModules;
import net.wurstclient.forge.utils.TextUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.css.RGBColor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public final class IngameHUD {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final HackList hackList;
	private final ClickGui clickGui;

	public static double theColor;

	public static float textColor;

	String color;

	public IngameHUD(HackList hackList, ClickGui clickGui) {
		this.hackList = hackList;
		this.clickGui = clickGui;
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.ALL || mc.gameSettings.showDebugInfo)
			return;

		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

		// color
		clickGui.updateColors();

		ClickGui gui = ForgeWurst.getForgeWurst().getGui();
		if (gui.getAcColor()[2] > gui.getAcColor()[1]) {
			textColor = 0x000AFF;

			clickGui.updateColors();
		} else if (gui.getAcColor()[1] > gui.getAcColor()[2]) {
			textColor = 0x11FF00;

			clickGui.updateColors();
		} else if (gui.getAcColor()[0] > gui.getAcColor()[1] && gui.getAcColor()[0] > gui.getAcColor()[2]) {
			textColor = 0xFF0000;

			clickGui.updateColors();
		}


		if (!ForgeWurst.getForgeWurst().getHax().clickGuiHack.nogui().isChecked()) {

			if (ForgeWurst.getForgeWurst().getHax().hudModules.isEnabled()) {
				if (ForgeWurst.getForgeWurst().getHax().hudModules.speed.isChecked()) {
					GL11.glPushMatrix();
					GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
					WMinecraft.getFontRenderer().drawStringWithShadow(String.format("%.3f", mc.player.motionX) + " " + String.format("%.3f", mc.player.motionY) + " " + String.format("%.3f", mc.player.motionZ), (int) HudModules.speedX, (int) HudModules.speedY, (int) IngameHUD.textColor);
					GL11.glPopMatrix();
				}
				if (ForgeWurst.getForgeWurst().getHax().hudModules.coords.isChecked()) {
					GL11.glPushMatrix();
					GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
					WMinecraft.getFontRenderer().drawStringWithShadow(Math.round(mc.player.posX) + " " + Math.round(mc.player.posY) + " " + Math.round(mc.player.posZ), (int) HudModules.coordX, (int) HudModules.coordY, (int) IngameHUD.theColor);
					GL11.glPopMatrix();
				}
			}

			// title
			GL11.glPushMatrix();
			GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
			WMinecraft.getFontRenderer().drawStringWithShadow("  allen", 3, 3, (int) textColor);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScaled(2, 2, 1);
			WMinecraft.getFontRenderer().drawStringWithShadow("F", 3, 3, (int) textColor);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
			WMinecraft.getFontRenderer().drawStringWithShadow(" _____", 3, 4, (int) textColor);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
			WMinecraft.getFontRenderer().drawStringWithShadow(" _____", 4, 4, (int) textColor);
			GL11.glPopMatrix();

			GL11.glShadeModel(GL11.GL_SMOOTH);

			// hack list
			int y = 23;
			ArrayList<Hack> hacks = new ArrayList<>(hackList.getValues());
			hacks.sort(Comparator.comparing(Hack::getName));

			gui.updateColors();

			for (Hack hack : hacks) {
				if (!hack.isEnabled())
					continue;

				if (hack.getName().equals(""))
					return;

				if (hack.getCategory().getName().contains("Combat")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.RED);
					theColor = Color.RED.getRGB();
				} else if (hack.getCategory().getName().contains("Movement")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.BLUE);
					theColor = Color.BLUE.getRGB();
				} else if (hack.getCategory().getName().contains("World")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.GREEN);
					theColor = Color.GREEN.getRGB();
				} else if (hack.getCategory().getName().contains("Player")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.GOLD);
					theColor = Color.ORANGE.getRGB();
				} else if (hack.getCategory().getName().contains("Render")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.AQUA);
					theColor = Color.CYAN.getRGB();
				} else if (hack.getCategory().getName().contains("Pathing")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.YELLOW);
					theColor = Color.YELLOW.getRGB();
				} else if (hack.getCategory().getName().contains("Games")) {
					color = TextUtil.coloredString(hack.getRenderName(), TextUtil.Color.GOLD);
					theColor = Color.YELLOW.getRGB();
				}

				WMinecraft.getFontRenderer().drawString(color, 4, y, (int) textColor, false);

				y += 9;
				// pinned windows
				if (!(mc.currentScreen instanceof ClickGuiScreen))
					clickGui.renderPinnedWindows(event.getPartialTicks());

				if (blend)
					GL11.glEnable(GL11.GL_BLEND);
				else
					GL11.glDisable(GL11.GL_BLEND);
			}
		}
	}
}