/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class QueueHelper extends Hack {

	private int lastPos;
	private int newPos;
	private int delay;
	private List<Integer> delays;

	private String content;

	public QueueHelper() {
		super("QueueHelper", "Helps you out while you're waiting in queues.");
		setCategory(Category.WORLD);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		delays = new ArrayList<>();
		delay = 0;
		lastPos = 0;
		newPos = 0;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		try {
			String s = mc.ingameGUI.getChatGUI().getSentMessages().get(mc.ingameGUI.getChatGUI().getSentMessages().size() - 3);
			int posInQueue = concatenateNumbersFromString(s);

			if (newPos == lastPos) {
				if (mc.player.ticksExisted % 20 == 0) {
					delay = delay + 1;
				}
			} else {
				lastPos = posInQueue;
				delays.add(delay);
				delay = 0;
			}

			content = convertSecondsToTime((int) getAverageFromArray(delays));
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onRenderGUI(RenderGameOverlayEvent.Post event) {
		try {
			GL11.glPushMatrix();
			GL11.glScaled(1.55555555, 1.55555555, 0.88888888);
			WMinecraft.getFontRenderer().drawStringWithShadow(content, 30, 30, (int) 0x00FFFF);
			GL11.glPopMatrix();
		} catch (Exception ignored) {
		}
	}

	public static String convertSecondsToTime(int totalSeconds) {
		if (totalSeconds < 0) {
			throw new IllegalArgumentException("Total seconds must be non-negative.");
		}

		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;

		return String.format("%dh %dm %ds", hours, minutes, seconds);
	}

	public static int concatenateNumbersFromString(String input) {
		StringBuilder numberBuilder = new StringBuilder();
		Pattern pattern = Pattern.compile("\\d+"); // Matches one or more digits
		Matcher matcher = pattern.matcher(input);

		while (matcher.find()) {
			numberBuilder.append(matcher.group());
		}

		String concatenatedNumberStr = numberBuilder.toString();
		int concatenatedNumber = 0;
		try {
			concatenatedNumber = Integer.parseInt(concatenatedNumberStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return concatenatedNumber;
	}

	private double getAverageFromArray(List<Integer> integers) {
		int sum = 0;

		for (int a : integers) {
			sum += a;
		}

		return (double) sum / integers.size();
	}
}