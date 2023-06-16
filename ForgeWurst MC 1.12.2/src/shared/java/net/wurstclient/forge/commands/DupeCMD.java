/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.InventoryUtil;

public final class DupeCMD extends Command {
    public DupeCMD() {
        super("dupe", "Dupe the item your currently holding.",
                "Syntax: .dupe");
    }

    @Override
    public void call(String[] args) throws CmdException {
        ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND).copy();
        double slot = InventoryUtil.getEmptySlot();
        mc.player.inventory.setInventorySlotContents((int) slot, itemStack);
    }
}