/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.clickgui.ClickGui;
import net.wurstclient.forge.clickgui.ClickGuiScreen;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.ClickGuiHack;
import net.wurstclient.forge.utils.TextUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

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

	static class NameLengthComparator implements Comparator<Hack> {
		@Override
		public int compare(Hack hack1, Hack hack2) {
			return Integer.compare(hack1.getName().length(), hack2.getName().length());
		}
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		if (event.getType() != ElementType.ALL || mc.gameSettings.showDebugInfo)
			return;

		boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

		// color
		clickGui.updateColors();

		ClickGui gui = ForgeWurst.getForgeWurst().getGui();
		float[] acColor = gui.getAcColor();

		double maxComponent = Math.max(acColor[0], Math.max(acColor[1], acColor[2]));

		if (acColor[2] == maxComponent) {
			textColor = 0x000AFF;
		} else if (acColor[1] == maxComponent) {
			textColor = 0x11FF00;
		} else if (acColor[0] == maxComponent) {
			// Change dark blue to light blue
			textColor = 0xADD8E6; // Light blue color
		} else {
			// Additional conditions and colors based on the range of double values
			if (acColor[0] > acColor[1] && acColor[0] > acColor[2]) {
				if (acColor[0] >= 0.5) {
					textColor = 0xFFFF00; // Yellow for high red values
				} else {
					textColor = 0xFFA500; // Orange for moderate red values
				}
			} else if (acColor[1] > acColor[2]) {
				if (acColor[1] >= 0.5) {
					textColor = 0x00FFFF; // Cyan for high green values
				} else {
					textColor = 0x00FF00; // Green for moderate green values
				}
			} else {
				if (acColor[2] >= 0.5) {
					textColor = 0xFF00FF; // Magenta for high blue values
				} else {
					textColor = 0x8A2BE2; // BlueViolet for moderate blue values
				}
			}
		}

		clickGui.updateColors();


		if (!ForgeWurst.getForgeWurst().getHax().clickGuiHack.nogui().isChecked()) {
			GL11.glPushMatrix();
			GL11.glScaled(2, 2, 1);
			WMinecraft.getFontRenderer().drawStringWithShadow("Fallen", ClickGuiHack.titleX.getValueI(), ClickGuiHack.titleY.getValueI(), (int) textColor);
			GL11.glPopMatrix();

			GL11.glShadeModel(GL11.GL_SMOOTH);

			// hack list
			int y = ClickGuiHack.arrayListY.getValueI();
			ArrayList<Hack> hacks = new ArrayList<>(hackList.getValues());
			Comparator<Hack> comparator = new NameLengthComparator();

			ScaledResolution scaledResolution = new ScaledResolution(mc);

			// Calculate the center of the screen
			int centerX = scaledResolution.getScaledWidth() / 2;

			if (centerX < ClickGuiHack.arrayListX.getValueI()) {
				hacks.sort(comparator);
			} else {
				hacks.sort(comparator.reversed());
			}

			gui.updateColors();

			for (Hack hack : hacks) {
				if (!hack.isEnabled())
					continue;

				if (hack.getName().equals(""))
					return;

				if (hack.getCategory() == Category.COMBAT) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.RED);
					theColor = Color.RED.getRGB();
				} else if (hack.getCategory() == Category.MOVEMENT) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.BLUE);
					theColor = Color.BLUE.getRGB();
				} else if (hack.getCategory() == Category.WORLD) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.GREEN);
					theColor = Color.GREEN.getRGB();
				} else if (hack.getCategory() == Category.PLAYER) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.GOLD);
					theColor = Color.ORANGE.getRGB();
				} else if (hack.getCategory() == Category.RENDER) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.AQUA);
					theColor = Color.CYAN.getRGB();
				} else if (hack.getCategory() == Category.PATHING) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.YELLOW);
					theColor = Color.YELLOW.getRGB();
				} else if (hack.getCategory() == Category.HUD) {
					color = TextUtils.coloredString(hack.getName(), TextUtils.Color.GOLD);
					theColor = Color.YELLOW.getRGB();
				}

				WMinecraft.getFontRenderer().drawString(color, ClickGuiHack.arrayListX.getValueI(), y, (int) textColor, false);
				y += 9;
			}
		}

		if (!(mc.currentScreen instanceof ClickGuiScreen))
			clickGui.renderPinnedWindows(event.getPartialTicks());
		if (blend)
			GL11.glEnable(GL11.GL_BLEND);
		else
			GL11.glDisable(GL11.GL_BLEND);
	}

	// Helper method to draw a bordered rectangle
	private void drawBorderedRect(int x, int y, int width, int height, int borderWidth) {
		//Gui.drawRect(x, y, x + width, y + height, boxColor);
		Gui.drawRect(x - borderWidth, y - borderWidth, x + width + borderWidth, y + height + borderWidth, -1072689136);
	}
}