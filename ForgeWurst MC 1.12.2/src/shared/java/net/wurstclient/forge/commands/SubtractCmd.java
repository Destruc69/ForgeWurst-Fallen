/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

public final class SubtractCmd extends Command
{
    public SubtractCmd()
    {
        super("subtract", "Subtract math.",
                "Syntax: .subtract <number> <number>");
    }

    @Override
    public void call(String[] args) throws CmdException
    {
        double answer = Integer.parseInt(args[0]) - Integer.parseInt(args[1]);
        ChatUtils.message(Integer.parseInt(args[0]) + " " + "Subtracted by" + " " + Integer.parseInt(args[1]) + " " + "equals" + " " + answer);
    }
}