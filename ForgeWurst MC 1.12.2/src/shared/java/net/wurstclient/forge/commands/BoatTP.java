/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.forge.Command;

import java.util.Objects;

public final class BoatTP extends Command
{
    public BoatTP()
    {
        super("boatTp", "Lets you teleport to a com.",
                "Syntax: .boatTp <x> <y> <z>");
    }

    @Override
    public void call(String[] args) throws CmdException
    {

        if(args.length != 3)
            throw new CmdSyntaxError();

        Objects.requireNonNull(mc.player.getRidingEntity()).setPosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}