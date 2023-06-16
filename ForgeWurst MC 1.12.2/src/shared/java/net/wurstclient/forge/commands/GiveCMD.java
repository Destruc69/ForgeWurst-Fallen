/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.InventoryUtil;

import java.util.Objects;

public final class GiveCMD extends Command {
    public GiveCMD() {
        super("give", "Give yourself items.",
                "Syntax: .give <id> <amount>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        Item item = Item.getItemById(Integer.parseInt(args[0]));
        double slot = InventoryUtil.getEmptySlot();
        mc.player.inventory.setInventorySlotContents((int)slot, new ItemStack(item));
        ItemStack itemStack = new ItemStack(item);
        itemStack.setCount(Integer.parseInt(args[1]));
    }
}