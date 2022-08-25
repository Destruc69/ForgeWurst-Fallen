/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WRenderBlockModelEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.*;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public final class Flight extends Hack {


	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.AAC);

	private final SliderSetting speed =
			new SliderSetting("Speed", "How fast we go for !specific modes!", 0.1, 0.05, 20, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private enum Mode {
		AAC("AAC", true, false, false, false, false, false, false),
		SPARTAN("Spartan", false, true, false, false, false, false, false),
		FLAGFLY("FlagFly", false, false, true, false, false, false, false),
		NCPJUMP("NCP-Jump", false, false, false, true, false, false, false),
		BASIC("Basic", false, false, false, false, true, false, false),
		HYPIXEL("Hypixel", false, false, false, false, false, true, false),
		MOTIONY("MotionY = 0", false, false, false, false, false, false, true);
		private final String name;
		private final boolean aac;
		private final boolean spartan;
		private final boolean flagfly;
		private final boolean ncpjump;
		private final boolean basic;
		private final boolean hypixel;
		private final boolean motiony0;

		private Mode(String name, boolean aac, boolean spartan, boolean flagfly, boolean ncpjump, boolean basic, boolean hypixel, boolean motiony0) {
			this.name = name;
			this.aac = aac;
			this.spartan = spartan;
			this.flagfly = flagfly;
			this.ncpjump = ncpjump;
			this.basic = basic;
			this.hypixel = hypixel;
			this.motiony0 = motiony0;
		}

		public String toString() {
			return name;
		}
	}


	public Flight() {
		super("Flight", "I believe i can fly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
		addSetting(speed);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (mode.getSelected().aac) {
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 1.7976931348623157E+308, mc.player.posZ, true));
		}
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().basic) {
			EntityPlayerSP player = event.getPlayer();

			player.capabilities.isFlying = false;
			player.motionX = 0;
			player.motionY = 0;
			player.motionZ = 0;
			player.jumpMovementFactor = speed.getValueF();

			if (mc.gameSettings.keyBindJump.isKeyDown())
				player.motionY += speed.getValueF();
			if (mc.gameSettings.keyBindSneak.isKeyDown())
				player.motionY -= speed.getValueF();


			if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
				if (mc.player.ticksExisted % 2 == 0) {
					mc.player.setPosition(mc.player.posX, mc.player.posY + 0.1235234, mc.player.posZ);
				} else {
					mc.player.setPosition(mc.player.posX, mc.player.posY - 0.1235234, mc.player.posZ);
				}
			}
		}
		if (mode.getSelected().motiony0) {
			mc.player.motionY = 0;
		}
		if (mode.getSelected().hypixel) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-5, mc.player.posZ);
			} else {
				mc.player.setVelocity(0, 0, 0);
			}
			mc.player.stepHeight = 0;
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		}
		if (mode.getSelected().aac) {
			mc.player.motionY = 0.003;
		}
		if (mode.getSelected().spartan) {
			mc.player.motionY = 0.264;
			if (mc.player.ticksExisted % 8 == 0) {
				mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 10, mc.player.posZ, true));
			}
		}
		if (mode.getSelected().flagfly) {
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX * 999, mc.player.posY + 0.00000001, mc.player.posZ + mc.player.motionZ * 999, true));
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX + mc.player.motionX * 11, mc.player.posY - 6969, mc.player.posZ + mc.player.motionZ * 11, true));
			mc.player.motionY = 0;
			mc.player.setPosition(mc.player.posX + mc.player.motionX * 11, mc.player.posY, mc.player.posZ + mc.player.motionZ * 11);
		}
		if (mode.getSelected().ncpjump) {
			mc.player.onGround = true;
			mc.player.isAirBorne = false;
			mc.player.fallDistance = 0;
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			assert event != null;
			assert event.getPacket() != null;
			try {
				if (mode.getSelected().aac) {
					SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
					assert sPacketPlayerPosLook != null;
					if (event.getPacket() instanceof SPacketPlayerPosLook) {
						event.setCanceled(true);
						mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), false));
						double dist = 0.14;
						double yaw = mc.player.rotationYaw;
						mc.player.setPosition(mc.player.posX + -sin(yaw) * dist, mc.player.posY, mc.player.posZ + cos(yaw) * dist);
						mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
						mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 1.7976931348623157E+308, mc.player.posZ, true));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mode.getSelected().basic || mode.getSelected().hypixel) {
				try {
					assert event != null;
					if (event.getPacket() instanceof CPacketPlayer) {
						event.setCanceled(true);
						mc.player.connection.sendPacket(new CPacketPlayer(true));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		assert event != null;
		assert event.getPacket() != null;
		try {
			if (mode.getSelected().aac) {
				SPacketPlayerPosLook sPacketPlayerPosLook = (SPacketPlayerPosLook) event.getPacket();
				assert sPacketPlayerPosLook != null;
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, sPacketPlayerPosLook.getYaw(), sPacketPlayerPosLook.getPitch(), false));
					double dist = 0.14;
					double yaw = mc.player.rotationYaw;
					mc.player.setPosition(mc.player.posX + -sin(yaw) * dist, mc.player.posY, mc.player.posZ + cos(yaw) * dist);
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, 1.7976931348623157E+308, mc.player.posZ, true));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}