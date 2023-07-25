/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WChatOutputEvent;
import net.wurstclient.forge.Command.CmdException;
import net.wurstclient.forge.utils.ChatUtils;

public final class CommandProcessor {
	private final CommandList cmds;

	private boolean isSyntaxMinus;

	public CommandProcessor(CommandList cmds) {
		this.cmds = cmds;
	}

	@SubscribeEvent
	public void onSentMessage(WChatOutputEvent event) {
		String message = event.getMessage().trim();
		if (!message.startsWith(".") && !message.startsWith("-")) // Separate the conditions using &&
			return;

		event.setCanceled(true);
		Minecraft.getMinecraft().ingameGUI.getChatGUI()
				.addToSentMessages(message);

		String commandWithoutPrefix = message.substring(1); // Remove only one character from the beginning
		if (message.startsWith(".")) {
			commandWithoutPrefix = message.substring(1); // Remove "." prefix
			isSyntaxMinus = false;
		} else if (message.startsWith("-")) {
			commandWithoutPrefix = message.substring(1); // Remove "-" prefix
			isSyntaxMinus = true;
		}

		runCommand(commandWithoutPrefix);
	}

	public void runCommand(String input) {
		String[] parts = input.split(" ");
		Command cmd = cmds.get(parts[0]);

		if (cmd == null) {
			if (!isSyntaxMinus) {
				ChatUtils.error("Unknown command: ." + parts[0]);
			} else {
				ChatUtils.error("Unknown command: -" + parts[0]);
			}
			if (!isSyntaxMinus) {
				ChatUtils.message("Type \".help\" for a list of commands or \".say ."
						+ input + "\" to send it as a chat message.");
			} else {
				ChatUtils.message("Type \"-help\" for a list of commands or \"-say -"
						+ input + "\" to send it as a chat message.");
			}
			return;
		}

		try {
			cmd.call(Arrays.copyOfRange(parts, 1, parts.length));

		} catch (CmdException e) {
			e.printToChat();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

