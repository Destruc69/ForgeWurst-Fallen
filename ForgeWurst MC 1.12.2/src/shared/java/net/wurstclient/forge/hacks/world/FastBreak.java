/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.world;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPlayerDamageBlockEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.PlayerControllerUtils;

public final class FastBreak extends Hack {
	private final CheckboxSetting damage =
			new CheckboxSetting("Damage", "Damage the block instead",
					false);

	public FastBreak() {
		super("FastBreak", "Allows you to break blocks faster.");
		setCategory(Category.WORLD);
		addSetting(damage);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) throws ReflectiveOperationException {
		try {
			if (!damage.isChecked()) {
				try {
					PlayerControllerUtils.setBlockHitDelay(0);

				} catch (ReflectiveOperationException e) {
					setEnabled(false);
					throw new RuntimeException(e);
				}
				BlockPos blockPos = mc.objectMouseOver.getBlockPos();
				mc.world.getBlockState(blockPos).getBlock().setHardness(mc.world.getBlockState(blockPos).getBlockHardness(mc.world, blockPos));
			} else {
				if (mc.playerController.getIsHittingBlock()) {
					BlockPos blockPos = mc.objectMouseOver.getBlockPos();
					mc.world.setBlockState(blockPos, Blocks.DIRT.getDefaultState());
				}
			}
		} catch (RuntimeException ignored) {
		}
	}

	@SubscribeEvent
	public void onPlayerDamageBlock(WPlayerDamageBlockEvent event) {
		try {
			if (!damage.isChecked()) {
				try {
					float progress = PlayerControllerUtils.getCurBlockDamageMP()
							+ BlockUtils.getHardness(event.getPos());

					if (progress >= 1)
						return;

				} catch (ReflectiveOperationException e) {
					setEnabled(false);
					throw new RuntimeException(e);
				}

				WMinecraft.getPlayer().connection.sendPacket(new CPacketPlayerDigging(
						CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(),
						event.getFacing()));
			}
		} catch (RuntimeException ignored) {
		}
	}
}