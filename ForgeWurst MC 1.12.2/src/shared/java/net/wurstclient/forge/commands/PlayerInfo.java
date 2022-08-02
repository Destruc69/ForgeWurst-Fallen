/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.world.GameType;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.Objects;

public final class PlayerInfo extends Command {
    public PlayerInfo() {
        super("info", "Get info of a player.",
                "Syntax: .info <player>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            String answer = args[0];
            double health = Objects.requireNonNull(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(answer)).getDisplayHealth();
            String gameType = Objects.requireNonNull(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(answer)).getGameType().getName();
            double responseTime = Objects.requireNonNull(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(answer)).getResponseTime();
            ChatUtils.message("Name: " + answer + " | " + "Health: " + health + " | " + "GameType: " + gameType + " | " + "ResponseTime: " + responseTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
