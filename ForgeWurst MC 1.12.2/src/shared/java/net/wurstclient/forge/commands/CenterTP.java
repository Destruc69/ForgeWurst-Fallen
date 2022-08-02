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

public final class CenterTP extends Command {
    public CenterTP() {
        super("center", "Center the players pos perfectly.",
                "Syntax: .center");
    }

    @Override
    public void call(String[] args) throws CmdException {
        mc.player.setPosition(Math.round(mc.player.posX), mc.player.posY, Math.round(mc.player.posZ));
    }
}