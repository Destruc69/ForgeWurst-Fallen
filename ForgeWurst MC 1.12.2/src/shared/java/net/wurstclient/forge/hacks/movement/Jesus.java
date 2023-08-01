/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WEntityPlayerJumpEvent;
import net.wurstclient.fmlevents.WGetLiquidCollisionBoxEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.compatibility.WPlayer;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.BlockUtils;

public final class Jesus extends Hack
{
	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	private int tickTimer;

	public Jesus()
	{
		super("Jesus", "Allows you to walk on water.\n"
				+ "Jesus used this hack ~2000 years ago.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	protected void onEnable()
	{
		MinecraftForge.EVENT_BUS.register(this);
		tickTimer = 2;
	}

	@Override
	protected void onDisable()
	{
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().normal) {
			EntityPlayerSP player = event.getPlayer();

			// check if sneaking
			if (player.isSneaking()
					&& GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
				return;

			// move up in water
			if (player.isInWater()) {
				player.motionY = 0.11;
				tickTimer = 0;
				return;
			}

			// simulate jumping out of water
			if (tickTimer == 0)
				player.motionY = 0.30;
			else if (tickTimer == 1)
				player.motionY = 0;

			// update timer
			tickTimer++;
		} else {
			if (mode.getSelected().remove) {
				for (int x = -mc.gameSettings.renderDistanceChunks * 15; x < mc.gameSettings.renderDistanceChunks * 15; x ++) {
					for (int y = -50; y < 50; y ++) {
						for (int z = -mc.gameSettings.renderDistanceChunks * 15; z < mc.gameSettings.renderDistanceChunks * 15; z ++) {
							BlockPos blockPos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
							if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.FLOWING_WATER)) {
								mc.world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPacketOutput(WPacketOutputEvent event)
	{
		if (mode.getSelected().normal) {
			// check packet type
			if (!(event.getPacket() instanceof CPacketPlayer))
				return;

			EntityPlayerSP player = WMinecraft.getPlayer();
			CPacketPlayer packet = (CPacketPlayer) event.getPacket();

			// check if packet contains a position
			if (!(packet instanceof CPacketPlayer.Position
					|| packet instanceof CPacketPlayer.PositionRotation))
				return;

			if (!isStandingOnLiquid(player))
				return;

			// if not actually moving, cancel packet
			if (player.movementInput == null) {
				event.setCanceled(true);
				return;
			}

			// get position
			double x = packet.getX(0);
			double y = packet.getY(0);
			double z = packet.getZ(0);

			// offset y
			if (player.ticksExisted % 2 == 0)
				y -= 0.05;
			else
				y += 0.05;

			// create new packet
			Packet<?> newPacket;
			if (packet instanceof CPacketPlayer.Position)
				newPacket = new CPacketPlayer.Position(x, y, z, true);
			else
				newPacket = new CPacketPlayer.PositionRotation(x, y, z,
						packet.getYaw(0), packet.getPitch(0), true);

			// send new packet
			event.setPacket(newPacket);
		}
	}

	@SubscribeEvent
	public void onGetLiquidCollisionBox(WGetLiquidCollisionBoxEvent event)
	{
		if (mode.getSelected().normal) {
			EntityPlayerSP player = WMinecraft.getPlayer();

			if (isLiquidCollisionEnabled(player))
				event.setSolidCollisionBox();
		}
	}

	@SubscribeEvent
	public void onEntityPlayerJump(WEntityPlayerJumpEvent event) {
		if (mode.getSelected().normal) {
			EntityPlayer player = event.getPlayer();
			if (player != WMinecraft.getPlayer())
				return;

			// Allow jump when pressing the sneak key but not actually sneaking.
			// This enables a glitch that allows the player to jump on water by
			// pressing the jump and sneak keys at the exact same time or by
			// pressing the sneak key while using BunnyHop.
			if (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)
					&& !player.isSneaking())
				return;

			if (!isStandingOnLiquid(player))
				return;

			event.setCanceled(true);
		}
	}

	private boolean isLiquidCollisionEnabled(EntityPlayer player)
	{
		if(player == null)
			return false;

		if(player.isSneaking()
				&& GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
			return false;

		if(player.isInWater() || player.fallDistance > 3)
			return false;

		return true;
	}

	private boolean isStandingOnLiquid(EntityPlayer player)
	{
		if(!isLiquidCollisionEnabled(player))
			return false;

		World world = WPlayer.getWorld(player);
		boolean foundLiquid = false;
		boolean foundSolid = false;

		// check collision boxes below player
		AxisAlignedBB playerBox = player.getEntityBoundingBox();
		playerBox = playerBox.union(playerBox.offset(0, -0.5, 0));
		// Using expand() with negative values doesn't work in 1.10.2.

		for(AxisAlignedBB box : world.getCollisionBoxes(player, playerBox))
		{
			BlockPos pos = new BlockPos(box.getCenter());
			Material material = BlockUtils.getMaterial(pos);

			if(material == Material.WATER || material == Material.LAVA)
				foundLiquid = true;
			else if(material != Material.AIR)
				foundSolid = true;
		}

		return foundLiquid && !foundSolid;
	}

	private enum Mode {
		NORMAL("Normal", true, false),
		REMOVE("Remove", false, true);

		private final String name;
		private final boolean normal;
		private final boolean remove;

		private Mode(String name, boolean normal, boolean remove) {
			this.name = name;
			this.normal = normal;
			this.remove = remove;
		}

		public String toString() {
			return name;
		}
	}
}