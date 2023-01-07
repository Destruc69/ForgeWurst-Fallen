/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.Command;

public final class SmartTPCmd extends Command {
    public static double posesToPacketX = 0;
    public static double posesToPacketZ = 0;

    public SmartTPCmd() {
        super("smarttp", "Tp that works on multiplayer servers..",
                "Syntax: .smarttp <x> <y> <z>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            if (x < mc.player.posX) {
                for (int a = x; a < mc.player.posX; a++) {
                    posesToPacketX = a;
                }
            } else {
                for (int a = x; a > mc.player.posX; a--) {
                    posesToPacketX = a;
                }
            }
            if (z < mc.player.posZ) {
                for (int b = z; b < mc.player.posZ; z++) {
                    posesToPacketZ = b;
                }
            } else {
                for (int b = z; z > mc.player.posZ; z--) {
                    posesToPacketZ = b;
                }
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(posesToPacketX, y, posesToPacketZ, true));
            mc.player.setPosition(x, y, z);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}