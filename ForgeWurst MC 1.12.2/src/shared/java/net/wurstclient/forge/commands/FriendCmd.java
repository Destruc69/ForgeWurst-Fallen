/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.FriendsList;
import net.wurstclient.forge.utils.ChatUtils;

public final class FriendCmd extends Command
{

	private static final FriendsList friendsList = ForgeWurst.getForgeWurst().getFriendsList();

	public FriendCmd()
	{
		super("friends", "A system to differ friends and non-friends in game.", "Syntax: .friend <add>|<remove> <name> <tag>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		String mode = String.valueOf(args[0]);
		String name = String.valueOf(args[1]);
		String tag = String.valueOf(args[2]);

		if (mode.equalsIgnoreCase("add")) {
			friendsList.add(name, tag);
			ChatUtils.message("Added " + name + " to the friends list.");
		} else if (mode.equalsIgnoreCase("remove")) {
			for (int i = 0; i < friendsList.size() - 1; i ++) {
				if (friendsList.get(i).getName().equalsIgnoreCase(name)) {
					friendsList.remove(i);
				}
			}
			ChatUtils.message("Removed " + name + " from the friends list.");
		} else {
			ChatUtils.message("Error.");
		}
	}
}
