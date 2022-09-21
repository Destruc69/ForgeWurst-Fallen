/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.commands;

import com.google.common.collect.Collections2;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.compatibility.WMinecraft;

import java.util.List;
import java.util.function.Predicate;

public final class PeekCmd extends Command {
	public PeekCmd() {
		super("peek", "View shulkers/chest without opening them.", "Syntax: .peek (hovering chest or shulker)");
	}

	@Override
	public void call(String[] args) throws CmdException {

	}
}
