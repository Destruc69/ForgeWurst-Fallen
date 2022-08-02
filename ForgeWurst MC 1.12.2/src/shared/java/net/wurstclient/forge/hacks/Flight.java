/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import net.wurstclient.forge.utils.MathUtils;
import net.wurstclient.forge.utils.TimerUtils;

import java.lang.reflect.Field;

public final class Flight extends Hack {

	private final EnumSetting<Mode> mode =
			new EnumSetting<>("Mode", Mode.values(), Mode.NORMAL);

	double startingPosFotSlimeY;
	double startingPosFotSlimeX;
	double startingPosFotSlimeZ;

	private enum Mode {
		NORMAL("Normal", true, false, false, false, false, false, false, false, false),
		HYPIXEL("Hypixel", false, true, false, false, false, false, false, false, false),
		MINEPLEX("Mineplex", false, false, true, false, false, false, false, false, false),
		ANTIKICK("AntiKick [PACKET]", false, false, false, true, false, false, false, false, false),
		ANTIKICKMOVE("AntiKick [MOVE]", false, false, false, false, true, false, false, false, false),
		BLINKFLY("BlinkFly", false, false, false, false, false, true, false, false, false),
		VOIDFLY("VoidFly", false, false,false, false, false, false, true, false, false),
		TEST("Test", false, false, false, false, false, false, false, true, false),
		SLIMEFLY("NCP-SlimeFly", false, false, false, false, false, false, false, false, true);


		private final String name;
		private final boolean normal;
		private final boolean hypixel;
		private final boolean mineplex;
		private final boolean antikick;
		private final boolean antikickmove;
		private final boolean blinkfly;
		private final boolean voidfly;
		private final boolean test;
		private final boolean slimefly;

		private Mode(String name, boolean normal, boolean hypixel, boolean mineplex, boolean antikick, boolean antikickmove, boolean blinkfly, boolean voidfly, boolean test, boolean slimefly) {
			this.name = name;
			this.normal = normal;
			this.hypixel = hypixel;
			this.mineplex = mineplex;
			this.antikick = antikick;
			this.antikickmove = antikickmove;
			this.blinkfly = blinkfly;
			this.voidfly = voidfly;
			this.test = test;
			this.slimefly = slimefly;
		}

		public String toString() {
			return name;
		}
	}

	@Override
	public String getRenderName() {
		return getName() + " [" + mode.getSelected().name() + "]";
	}

	public Flight() {
		super("Flight", "I believe i can fly.");
		setCategory(Category.MOVEMENT);
		addSetting(mode);
	}

	@Override
	protected void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		TimerUtils.reset();
		startingPosFotSlimeY = mc.player.posY;
		startingPosFotSlimeX = mc.player.posX;
		startingPosFotSlimeZ = mc.player.posZ;
	}

	@Override
	protected void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		if (mode.getSelected().blinkfly) {
			mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
		}
		if (mode.getSelected().hypixel || mode.getSelected().test) {
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, mc.gameSettings.keyBindJump.isKeyDown());
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSneak.isKeyDown());
			KeyBindingUtils.setPressed(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindForward.isKeyDown());
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		if (mode.getSelected().normal) {
			normalFly();
		}
		if (mode.getSelected().hypixel) {
			hypixelFly();
		}
		if (mode.getSelected().mineplex) {
			mineplexFly();
		}
		if (mode.getSelected().antikick) {
			antiKickFly();
		}
		if (mode.getSelected().antikickmove) {
			antiKickMoveFly();
		}
		if (mode.getSelected().blinkfly) {
			blinkFly();
		}
		if (mode.getSelected().voidfly) {
			voidFly();
		}
		if (mode.getSelected().test) {
			testFly();
		}
		if (mode.getSelected().slimefly) {
			slimeFly();
		}
	}

	public void slimeFly() {
		if (mc.world.getBlockState(new BlockPos(startingPosFotSlimeX, startingPosFotSlimeY - 1, startingPosFotSlimeZ)).getBlock().equals(Blocks.SLIME_BLOCK)) {
			if (!(mc.player.fallDistance > 0.5)) {
				if (mc.player.onGround) {
					mc.player.motionY = 0.5;
				}
			} else {
				mc.player.motionY = 0;
			}
		}
	}

	public void testFly() {
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
		mc.player.setPosition(mc.player.posX, mc.player.posY + 0.0001, mc.player.posZ);
		mc.player.motionY = 0;
	}

	public void voidFly() {
		if (mc.player.hurtTime > 0) {
			mc.player.motionY = 0.405 * 8;
		}
	}

	public void blinkFly() {
		EntityPlayerSP player = mc.player;
		mc.player.motionY = 0;
		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = 1;
	}

	public void antiKickMoveFly() {
		EntityPlayerSP player = mc.player;

		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = 1;

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += 1;
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= 1;

		if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
			if (mc.player.ticksExisted % 2 == 0) {
				mc.player.setPosition(mc.player.posX, mc.player.posY - 0.1, mc.player.posZ);
			} else {
				mc.player.setPosition(mc.player.posX, mc.player.posY + 0.05, mc.player.posZ);
			}
		}
	}

	public void antiKickFly() {
		EntityPlayerSP player = mc.player;

		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = 1;

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += 1;
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= 1;
	}

	public void normalFly() {
		EntityPlayerSP player = mc.player;

		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = 1;

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += 1;
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= 1;
	}

	public void hypixelFly() {
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindJump, false);
		KeyBindingUtils.setPressed(mc.gameSettings.keyBindSneak, false);
		if (mc.player.ticksExisted % 2 == 0) {
			mc.player.setPosition(mc.player.posX, mc.player.posY + 0.1, mc.player.posZ);
		} else {
			TimerUtils.reset();
			mc.player.motionY = 0;
		}
	}

	public void mineplexFly() {
		if (mc.player.getHeldItemMainhand().getItem().equals(Blocks.AIR))
			return;

		BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
		EnumFacing enumFacing = EnumFacing.UP;

		if (TimerUtils.hasReached(100)) {
			TimerUtils.reset();
			if (mc.world.getBlockState(mc.player.getPosition().add(0, -0.6, 0)).getBlock().equals(Blocks.AIR)) {
				mc.player.setPosition(mc.player.posX, mc.player.posY - 0.6, mc.player.posZ);
			}
		}

		mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing, EnumHand.MAIN_HAND, (float) mc.player.getLookVec().x, (float) mc.player.getLookVec().y, (float) mc.player.getLookVec().z));

		EntityPlayerSP player = mc.player;

		player.capabilities.isFlying = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = 1;

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += 1;
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= 1;
	}


	@SubscribeEvent
	public void onPacket(WPacketInputEvent event) {
		try {
			if (mode.getSelected().hypixel || mode.getSelected().mineplex || mode.getSelected().antikick) {
				if (event.getPacket() instanceof CPacketPlayer.Position) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
				}
				if (event.getPacket() instanceof CPacketPlayer.PositionRotation) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
				}
				if (event.getPacket() instanceof CPacketPlayer.Rotation) {
					event.setCanceled(true);
					mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true));
				}
			}
			if(mode.getSelected().blinkfly) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if(mode.getSelected().blinkfly) {
				if (event.getPacket() instanceof SPacketPlayerPosLook) {
					event.setCanceled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}