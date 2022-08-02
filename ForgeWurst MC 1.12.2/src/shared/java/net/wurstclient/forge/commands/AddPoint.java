/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.hacks.Pointer;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.Objects;

public final class AddPoint extends Command {
    public AddPoint() {
        super("addPoint", "Adds a pointer at a coord.",
                "Syntax: .addPoint <x> <y> <x>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            Pointer.blockPos.clear();
            String x = args[0];
            String y = args[1];
            String z = args[2];
            Pointer.blockPos.add(new BlockPos(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
            ChatUtils.message("[AP] Set pointer to " + x + " " + y + " " + z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
