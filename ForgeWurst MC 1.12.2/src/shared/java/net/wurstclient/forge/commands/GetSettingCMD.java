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
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.ChatUtils;

public final class GetSettingCMD extends Command
{
	public GetSettingCMD()
	{
		super("getsetting", "Obtains all settings from a module.", "Syntax: .getsetting <hack>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		Hack hack = ForgeWurst.getForgeWurst().getHax().get(String.valueOf(args[0]));

		if (hack != null) {
			ChatUtils.message("------------------");
			ChatUtils.message("Setting for module " + hack.getName());
			for (String s : hack.getSettings().keySet()) {
				ChatUtils.message(s);
			}
			ChatUtils.message("------------------");
		} else {
			ChatUtils.message("Null-Pointer, misspell?");
		}
	}
}
