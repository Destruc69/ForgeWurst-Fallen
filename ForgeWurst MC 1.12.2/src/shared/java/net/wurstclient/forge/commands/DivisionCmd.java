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

public final class DivisionCmd extends Command
{
    public DivisionCmd()
    {
        super("divide", "Multiply division.",
                "Syntax: .divide <number> <number>");
    }

    @Override
    public void call(String[] args) throws CmdException
    {

        if (args.length != 2)
            throw new CmdSyntaxError();

        double answer = Integer.parseInt(args[0]) / Integer.parseInt(args[1]);
        ChatUtils.message(Integer.parseInt(args[0]) + " " + "divided by" + " " + Integer.parseInt(args[1]) + " " + "equals" + " " + answer);
    }
}