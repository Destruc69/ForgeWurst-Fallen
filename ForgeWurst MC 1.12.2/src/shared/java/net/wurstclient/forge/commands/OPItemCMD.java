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

public final class OPItemCMD extends Command {
    public OPItemCMD() {
        super("opitem", "Makes your currently holding item god like.",
                "Syntax: .opitem");
    }

    @Override
    public void call(String[] args) throws CmdException {
        try {
            ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            for (int a = 0; a < 155; a++) {
                itemStack.addEnchantment(Objects.requireNonNull(Enchantment.getEnchantmentByID(a)), Objects.requireNonNull(Enchantment.getEnchantmentByID(a)).getMaxLevel());
            }
        } catch (Exception ignored) {
        }
    }
}