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

public final class SpartanTP extends Command
{
    public SpartanTP()
    {
        super("spartantp", "Lets you teleport to a com (Spartan AC).",
                "Syntax: .spartantp  <plus y>");
    }

    @Override
    public void call(String[] args) throws CmdException
    {

        if(args.length != 3)
            throw new CmdSyntaxError();

        for (int x = 0; x < 10; x ++) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + Double.parseDouble(args[1]), mc.player.posZ, true));
        }
    }
}