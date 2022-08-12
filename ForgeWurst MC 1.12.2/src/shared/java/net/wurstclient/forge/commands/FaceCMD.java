/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.RotationUtils;

public final class FaceCMD extends Command {
    public FaceCMD() {
        super("face", "Face the player towards a coord for pathing",
                "Syntax: .face <x> <z>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        double xCoord = Integer.parseInt(args[0]);
        double zCoord = Integer.parseInt(args[2]);

        float[] rot = RotationUtils.getNeededRotations(new Vec3d(xCoord, mc.player.posY, zCoord));
        mc.player.rotationYaw = rot[0];

        try {
            ChatUtils.message("[FACECMD] Pathing modules may change yaw, only rely on them for easy terrain and to get close to your destanation, In the future i will make it compatible to aim for coords");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}