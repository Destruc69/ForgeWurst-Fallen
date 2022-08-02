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
import net.wurstclient.forge.utils.TimerUtils;

import java.util.Objects;

public final class Damage extends Command {
    public Damage() {
        super("damage", "Damage yourself.",
                "Syntax: .damage");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            if (TimerUtils.hasReached(10)) {
                mc.player.setPosition(mc.player.posX, mc.player.posY + 1, mc.player.posZ);
            } else {
                mc.player.motionY -= 999;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}