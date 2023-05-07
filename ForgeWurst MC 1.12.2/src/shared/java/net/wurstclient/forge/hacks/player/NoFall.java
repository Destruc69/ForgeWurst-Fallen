/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.player;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.movement.AutoSprintHack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public final class NoFall extends Hack {

	private final SliderSetting fallDistance =
			new SliderSetting("FallDistance [ANTIFALL]", "If we exeed this value we will prevent you from falling [FOR ANTI-FALL]", 4, 1, 50, 1, SliderSetting.ValueDisplay.DECIMAL);

	public static double lastOnGroundX;
	public static double lastOnGroundY;
	public static double lastOnGroundZ;

	public static double lastOnGroundXR;
	public static double lastOnGroundYR;
	public static double lastOnGroundZR;

	public static Vec3d lastOnGroundLeave;

	private final EnumSetting<ModeRideable> modeRideable =
			new EnumSetting<>("Rideables", ModeRideable.values(), ModeRideable.DAMAGE);

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.PACKET);

	public NoFall() {
		super("NoFall", "Prevents falling damage/falling.");
		setCategory(Category.PLAYER);
		addSetting(mode);
		addSetting(fallDistance);
		addSetting(modeRideable);
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
		if (mode.getSelected().packet) {
			if (mc.player.fallDistance > 4) {
				mc.player.connection.sendPacket(new CPacketPlayer(true));
			}
		}

		if (mode.getSelected().aac) {
			if (mc.player.fallDistance > 2) {
				mc.player.motionZ = 0;
				mc.player.motionX = mc.player.motionZ;
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10E-4, mc.player.posZ, mc.player.onGround));
				mc.player.connection.sendPacket(new CPacketPlayer(true));
			}
		}

		if (mode.getSelected().anti) {
			if (mc.player.onGround) {
				lastOnGroundX = mc.player.lastTickPosX;
				lastOnGroundY = mc.player.lastTickPosY;
				lastOnGroundZ = mc.player.lastTickPosZ;
			}
			if (mc.player.fallDistance > fallDistance.getValueF()) {
				mc.player.setPosition(lastOnGroundX, lastOnGroundY, lastOnGroundZ);
			}
		}
		if (mode.getSelected().damage) {
			if (mc.player.fallDistance > 4) {
				mc.player.onGround = true;
			}
		}

		try {
			if (modeRideable.getSelected().damage) {
				if (Objects.requireNonNull(mc.player.getRidingEntity()).fallDistance > 4) {
					Objects.requireNonNull(mc.player.getRidingEntity()).onGround = true;
				}
			} else if (modeRideable.getSelected().anti) {
				if (Objects.requireNonNull(mc.player.getRidingEntity()).onGround) {
					lastOnGroundXR = mc.player.getRidingEntity().lastTickPosX;
					lastOnGroundYR = mc.player.getRidingEntity().lastTickPosY;
					lastOnGroundZR = mc.player.getRidingEntity().lastTickPosZ;
				}
				if (mc.player.getRidingEntity().fallDistance > fallDistance.getValueF()) {
					mc.player.getRidingEntity().setPosition(lastOnGroundXR, lastOnGroundYR, lastOnGroundZR);
				}
			}
		} catch (Exception ignored) {
		}

		try {
			if (mode.getSelected().leave) {
				if (mc.player.onGround) {
					lastOnGroundLeave = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
				} else {
					if (!mc.world.getBlockState(mc.player.getPosition().add(0, -2, 0)).getBlock().equals(Blocks.AIR) && mc.player.getDistance(mc.player.lastTickPosX, lastOnGroundLeave.y, mc.player.lastTickPosZ) > 4) {
						mc.player.connection.onDisconnect(new TextComponentString("[LEAVE-NOFALL] Rejoin and your fall distance will be dismissed."));
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	private enum Mode {
		PACKET("Packet", true, false, false, false, false),
		ANTI("Anti", false, true, false, false, false),
		DAMAGE("Damage", false, false, false, true, false),
		AAC("AAC", false, false, true, false, false),
		LEAVE("Leave", false, false, false, false, true);

		private final String name;
		private final boolean packet;
		private final boolean anti;
		private final boolean aac;
		private final boolean damage;
		private final boolean leave;

		private Mode(String name, boolean packet, boolean anti, boolean aac, boolean damage, boolean leave) {
			this.name = name;
			this.anti = anti;
			this.packet = packet;
			this.aac = aac;
			this.damage = damage;
			this.leave = leave;
		}

		public String toString() {
			return name;
		}
	}

	private enum ModeRideable {
		DAMAGE("Damage", true, false, false),
		ANTI("Anti", false, true, false),
		OFF("Off", false, false, true);

		private final String name;
		private final boolean damage;
		private final boolean anti;
		private final boolean off;

		private ModeRideable(String name, boolean damage, boolean anti, boolean off) {
			this.name = name;
			this.anti = anti;
			this.damage = damage;
			this.off = off;
		}

		public String toString() {
			return name;
		}
	}
}