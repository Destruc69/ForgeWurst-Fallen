/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RenderUtils;
import net.wurstclient.forge.utils.RotationUtils;
import net.wurstclient.forge.utils.TimerUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class NoFall extends Hack {

	private final SliderSetting fallDistance =
			new SliderSetting("FallDistance [ANTIFALL]", "If we exeed this value we will prevent you from falling [FOR ANTI-FALL]", 4, 1, 50, 1, SliderSetting.ValueDisplay.DECIMAL);


	public static double lastOnGroundX;
	public static double lastOnGroundY;
	public static double lastOnGroundZ;

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.PACKET);

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
				lastOnGroundX = mc.player.posX;
				lastOnGroundY = mc.player.posY;
				lastOnGroundZ = mc.player.posZ;
			}
			if (mc.player.fallDistance > fallDistance.getValueF()) {
				mc.player.setPosition(lastOnGroundX, lastOnGroundY, lastOnGroundZ);
			}
		}
	}

	private enum Mode {
		PACKET("Packet", true, false, false),
		ANTI("Anti", false, true, false),
		AAC("AAC", false, false, true);

		private final String name;
		private final boolean packet;
		private final boolean anti;
		private final boolean aac;

		private Mode(String name, boolean packet, boolean anti, boolean aac) {
			this.name = name;
			this.anti = anti;
			this.packet = packet;
			this.aac = aac;
		}

		public String toString() {
			return name;
		}
	}
}