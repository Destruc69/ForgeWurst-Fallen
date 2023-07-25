/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.*;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.FallenRenderUtils;

import java.awt.*;

public final class FreeCam extends Hack {

	private Vec3d playerPosSave;

	private final SliderSetting speed =
			new SliderSetting("Speed", 1, 0.05, 10, 0.05, SliderSetting.ValueDisplay.DECIMAL);

	private final CheckboxSetting teleport =
			new CheckboxSetting("Teleport", "Teleports you to where you disabled the module.",
					false);

	public FreeCam() {
		super("FreeCam", "Go outside of your body.");
		setCategory(Category.RENDER);
		addSetting(speed);
		addSetting(teleport);
	}

	@Override
	public String getRenderName()
	{
		return getName() + " [" + speed.getValueString() + "]";
	}

	@Override
	public void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
		if (!teleport.isChecked()) {
			playerPosSave = new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ);
		}
	}

	@Override
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		if (!teleport.isChecked()) {
			mc.player.setPosition(playerPosSave.x, playerPosSave.y, playerPosSave.z);
		}
	}

	@SubscribeEvent
	public void onUpdate(WUpdateEvent event) {
		EntityPlayerSP player = event.getPlayer();

		player.onGround = false;
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		player.jumpMovementFactor = speed.getValueF();

		if (mc.gameSettings.keyBindJump.isKeyDown())
			player.motionY += speed.getValue();
		if (mc.gameSettings.keyBindSneak.isKeyDown())
			player.motionY -= speed.getValue();
	}

	@SubscribeEvent
	public void onRender(RenderWorldLastEvent event) {
		if (!teleport.isChecked()) {
			FallenRenderUtils.drawLine(new Vec3d(playerPosSave.x, playerPosSave.y, playerPosSave.z), new Vec3d(playerPosSave.x, playerPosSave.y + 1, playerPosSave.z), 12, Color.GREEN);
		}
	}

	@SubscribeEvent
	public void onRenderHand(RenderHandEvent event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onPlayerMove(WPlayerMoveEvent event) {
		event.getPlayer().noClip = true;
	}

	@SubscribeEvent
	public void onIsNormalCube(WIsNormalCubeEvent event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onSetOpaqueCube(WSetOpaqueCubeEvent event) {
		event.setCanceled(true);
	}


	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
				event.setCanceled(true);
			}
		} catch (Exception ignored) {
		}
	}
}