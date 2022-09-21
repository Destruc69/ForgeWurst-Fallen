/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.utils;

import com.mojang.text2speech.Narrator;
import com.mojang.text2speech.NarratorDummy;
import com.mojang.text2speech.NarratorWindows;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.wurstclient.forge.Hack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TTS {

	public static Narrator narrator = new NarratorWindows();

	public void TTS() {

	}

	public static void say(String message) {
		narrator.say(message);
	}

	public static void clear() {
		narrator.clear();
	}

	public static void active() {
		narrator.active();
	}
}

