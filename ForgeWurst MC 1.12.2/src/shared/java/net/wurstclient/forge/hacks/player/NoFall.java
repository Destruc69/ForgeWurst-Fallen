/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public final class NoFall extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.PACKET);

	private final SliderSetting fallDistance =
			new SliderSetting("FallDistance [ANTIFALL]", "If we exeed this value we will prevent you from falling [FOR ANTI-FALL]", 4, 1, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	private Vec3d vec3d;

	public NoFall() {
		super("NoFall", "Prevents falling damage/falling.");
		setCategory(Category.PLAYER);
		addSetting(mode);
		addSetting(fallDistance);
	}

	@Override
	public String getRenderName() {
		return getName() + " [" + mode.getSelected().name() + "]";
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
	public void onUpdate(WUpdateEvent event) {
		if (mc.player.getRidingEntity() == null) {
			if (mode.getSelected() == Mode.PACKET) {
				if (mc.player.fallDistance > 4) {
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			} else if (mode.getSelected() == Mode.DAMAGE) {
				if (mc.player.fallDistance > 4) {
					mc.player.onGround = true;
				}
			} else if (mode.getSelected() == Mode.ANTI) {
				if (mc.player.onGround) {
					vec3d = new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
				} else {
					if (mc.player.fallDistance > fallDistance.getValueF()) {
						mc.player.setPosition(vec3d.x, vec3d.y, vec3d.z);
					}
				}
			} else if (mode.getSelected() == Mode.AAC) {
				if (mc.player.fallDistance > 2) {
					mc.player.motionZ = 0;
					mc.player.motionX = mc.player.motionZ;
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10E-4, mc.player.posZ, mc.player.onGround));
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			}
		} else {
			if (mode.getSelected() == Mode.PACKET) {
				if (mc.player.getRidingEntity().fallDistance > 4) {
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			} else if (mode.getSelected() == Mode.DAMAGE) {
				if (mc.player.getRidingEntity().fallDistance > 4) {
					mc.player.getRidingEntity().onGround = true;
				}
			} else if (mode.getSelected() == Mode.ANTI) {
				if (mc.player.getRidingEntity().onGround) {
					vec3d = new Vec3d(mc.player.getRidingEntity().lastTickPosX, mc.player.getRidingEntity().lastTickPosY, mc.player.getRidingEntity().lastTickPosZ);
				} else {
					if (mc.player.getRidingEntity().fallDistance > fallDistance.getValueF()) {
						mc.player.getRidingEntity().setPosition(vec3d.x, vec3d.y, vec3d.z);
					}
				}
			} else if (mode.getSelected() == Mode.AAC) {
				if (mc.player.getRidingEntity().fallDistance > 2) {
					mc.player.getRidingEntity().motionZ = 0;
					mc.player.getRidingEntity().motionX = mc.player.motionZ;
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10E-4, mc.player.posZ, mc.player.onGround));
					mc.player.connection.sendPacket(new CPacketPlayer(true));
				}
			}
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		if (mode.getSelected() == Mode.CANCEL) {
			if (mc.player.fallDistance > 4) {
				if (event.getPacket() instanceof CPacketPlayer) {
					event.setCanceled(true);
				}
			}
		}
	}

	private enum Mode {
		PACKET("Packet"),
		ANTI("Anti"),
		DAMAGE("Damage"),
		AAC("AAC"),
		CANCEL("Cancel");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
}