/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WPacketInputEvent;
import net.wurstclient.fmlevents.WPacketOutputEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.EnumSetting;
import org.lwjgl.input.Keyboard;

public final class InvMove extends Hack {

	private final EnumSetting<Mode> bypass =
			new EnumSetting<>("BypassMethod", Mode.values(), Mode.A);

	public InvMove() {
		super("InvMove", "Allows you to move with your inventory.");
		setCategory(Category.MOVEMENT);
		addSetting(bypass);
	}

	private enum Mode {
		A("A", true, false),
		B("B", false, true);

		private final String name;
		private final boolean a;
		private final boolean b;

		private Mode(String name, boolean a, boolean b) {
			this.name = name;
			this.a = a;
			this.b = b;
		}

		public String toString() {
			return name;
		}
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
	public void onInput(InputUpdateEvent event) {
		assert event != null;
		try {
			if (mc.currentScreen instanceof GuiChat)
				return;
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				event.getMovementInput().moveForward++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				event.getMovementInput().moveForward--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				event.getMovementInput().moveStrafe--;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				event.getMovementInput().moveStrafe++;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				mc.player.rotationYaw = mc.player.rotationYaw + 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				mc.player.rotationYaw = mc.player.rotationYaw - 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				mc.player.rotationPitch = mc.player.rotationPitch - 3;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				mc.player.rotationPitch = mc.player.rotationPitch + 3;
			}
			event.getMovementInput().jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
			event.getMovementInput().sneak = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

			if (bypass.getSelected().b) {
				if (mc.player.ticksExisted % 20 == 0) {
					mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.openContainer.windowId));
				}
			}
		} catch (Exception ignored) {
		}
	}

	@SubscribeEvent
	public void onPacketIn(WPacketInputEvent event) {
		try {
			if (bypass.getSelected().a) {
				if (event.getPacket() instanceof CPacketEntityAction) {
					CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
					if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
						event.setCanceled(true);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}
	@SubscribeEvent
	public void onPacketOut(WPacketOutputEvent event) {
		try {
			if (bypass.getSelected().a) {
				if (event.getPacket() instanceof CPacketEntityAction) {
					CPacketEntityAction cPacketEntityAction = (CPacketEntityAction) event.getPacket();
					if (cPacketEntityAction.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY)) {
						event.setCanceled(true);
					}
				}
			}
		} catch (Exception ignored) {
		}
	}
}