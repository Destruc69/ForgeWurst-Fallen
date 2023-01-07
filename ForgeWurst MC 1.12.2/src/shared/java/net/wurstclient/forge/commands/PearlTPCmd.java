/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

public final class PearlTPCmd extends Command {
    public PearlTPCmd() {
        super("pearltp", "Tp that works with pearls.",
                "Syntax: .pearltp <x> <y> <z>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.ENDER_PEARL)) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(x, y, z), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0, 0, 0));
                mc.player.swingArm(EnumHand.MAIN_HAND);
            } else {
                try {
                    ChatUtils.error("You need to be holding a pearl to do the pearl tp.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}