/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.wurstclient.forge.Command;

import java.util.Objects;

public final class EnchantCMD extends Command {
    public EnchantCMD() {
        super("enchant", "Allows you to enchant your currently holding item.",
                "Syntax: .enchant <id> <level>");
    }

    @Override
    public void call(String[] args) throws CmdException {
        ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

        String id = args[0];
        String level = args[1];

        itemStack.addEnchantment(Objects.requireNonNull(Enchantment.getEnchantmentByID(Integer.parseInt(id))), Integer.parseInt(level));
    }
}