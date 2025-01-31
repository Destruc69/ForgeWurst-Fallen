/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.KeyBindingUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShulkerViewer extends Hack {

	private String name = "";
	private boolean box = false;


	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

	public ShulkerViewer() {
		super("Peek", "Shows you whats inside a shulker, thank you spect client by beloved.");
		setCategory(Category.RENDER);
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
	public void ItemTooltipEvent(ItemTooltipEvent event) {
		if (this.box) {
			event.getToolTip().clear();
			event.getToolTip().add(this.name);
		}
		if (event.getItemStack().getItem() instanceof ItemMap) {
			event.getToolTip().clear();
			event.getToolTip().add(event.getItemStack().getDisplayName());
		}
	}

	@SubscribeEvent
	public void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		if (event.getGui() instanceof GuiContainer && ((GuiContainer) event.getGui()).getSlotUnderMouse() != null) {
			// Shulker Peek
			ItemStack item = Objects.requireNonNull(((GuiContainer) event.getGui()).getSlotUnderMouse()).getStack();
			if (item.getItem() instanceof ItemShulkerBox) {
				this.name = item.getDisplayName();
				this.box = true;
				int X = event.getMouseX() + 8;
				int Y = event.getMouseY() - 82;

				NBTTagList nbttaglist = (Objects.requireNonNull(item.hasTagCompound() ? item.getTagCompound() : new NBTTagCompound())).getCompoundTag("BlockEntityTag").getTagList("Items", 10);
				int xMod = 6;
				int yMod = 6;
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GL11.glDisable(2929);
				GlStateManager.color(2.0F, 2.0F, 2.0F, 1.0F);
				mc.getTextureManager().bindTexture(new ResourceLocation(ForgeWurst.MODID, "assets/shulker.png"));
				mc.ingameGUI.drawTexturedModalRect(X, Y, 0, 0, 172, 64);
				GL11.glEnable(2929);


				for (NBTBase list : nbttaglist) {
					ItemStack stack = new ItemStack((NBTTagCompound) list);
					String stringofitems = list.toString();
					int slotnum = Integer.parseInt(stringofitems.substring(stringofitems.lastIndexOf("{Slot:") + 1, stringofitems.indexOf("b,")).replaceAll("[^0-9]", ""));
					if (slotnum > 8 && slotnum < 17) {
						xMod = -156;
						yMod = 24;
					} else if (slotnum > 17) {
						xMod = -318;
						yMod = 42;
					}

					GlStateManager.pushMatrix();
					GL11.glDepthFunc(517);
					RenderHelper.disableStandardItemLighting();
					enableGUIStandardItemLighting();
					mc.getRenderItem().renderItemAndEffectIntoGUI(stack, X + xMod + 18 * slotnum, Y + yMod);
					String string = Integer.toString(stack.getCount());

					if (stack.getCount() == 1) {
						string = "";
					}
					mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, X + xMod + 18 * slotnum, Y + yMod, string);
					GlStateManager.popMatrix();
				}

				GL11.glDepthFunc(515);
			}
			// Map Peek
			if (mc.player.inventory.getItemStack().getItem() instanceof ItemAir) {
				Slot slotUnderMouse = ((GuiContainer) event.getGui()).getSlotUnderMouse();
				if (slotUnderMouse != null && slotUnderMouse.getHasStack()) {
					ItemStack itemUnderMouse = slotUnderMouse.getStack();
					if (itemUnderMouse.getItem() instanceof ItemMap) {
						MapData mapdata = ((ItemMap) itemUnderMouse.getItem()).getMapData(itemUnderMouse, mc.world);
						if (mapdata != null) {
							GlStateManager.disableDepth();
							GlStateManager.disableLighting();
							mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
							Tessellator tessellator = Tessellator.getInstance();
							BufferBuilder bufferbuilder = tessellator.getBuffer();
							GlStateManager.translate(event.getMouseX(), event.getMouseY() + 15.5D, 0.0D);
							GlStateManager.scale(0.5D, 0.5D, 1.0D);
							bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
							bufferbuilder.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
							bufferbuilder.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
							bufferbuilder.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
							bufferbuilder.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
							tessellator.draw();
							mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
							GlStateManager.enableLighting();
							GlStateManager.enableDepth();
						}
					}
				}
			}
		} else {
			this.box = false;
		}
	}

	private void enableGUIStandardItemLighting() {
	}


	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		try {
			ItemStack itemStack = mc.player.getHeldItemMainhand();
			if (itemStack.getItem() instanceof ItemShulkerBox && Mouse.getEventButton() == 1) {
				Peekcode(itemStack, mc);
			}
		} catch (Exception ignored) {
		}
	}

	public static void Peekcode(ItemStack itemStack, Minecraft mc) {
		TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
		entityBox.getBlockType().getBlockState().getBlock().equals(((ItemShulkerBox) itemStack.getItem()).getBlock());
		entityBox.setWorld(mc.world);
		assert itemStack.getTagCompound() != null;
		entityBox.readFromNBT(itemStack.getTagCompound().getCompoundTag("BlockEntityTag"));
		entityBox.setCustomName("Shulker Peek");
		mc.player.displayGUIChest(entityBox);
	}
}