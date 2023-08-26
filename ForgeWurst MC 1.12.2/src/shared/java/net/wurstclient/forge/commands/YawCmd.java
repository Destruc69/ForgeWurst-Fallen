/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.wurstclient.forge.Command;
import scala.Int;

public final class YawCmd extends Command
{
	public YawCmd()
	{
		super("yaw", "Sets your yaw.", "Syntax: .yaw <value>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		mc.player.rotationYaw = Integer.parseInt(args[0]);
	}
}
