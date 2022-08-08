/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.utils.MathUtils;

public final class tpCMD extends Command
{
    public tpCMD()
    {
        super("tp", "Lets you teleport to a com.",
                "Syntax: .tp <x> <y> <z>");
    }

    @Override
    public void call(String[] args) throws CmdException
    {

        if(args.length != 3)
            throw new CmdSyntaxError();

        for (int x = 0; x < 6; x ++) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), mc.player.onGround));

        }
        mc.player.setPosition(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }
}