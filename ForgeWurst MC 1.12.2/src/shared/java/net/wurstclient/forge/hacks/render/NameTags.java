/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public final class NameTags extends Hack {

	ArrayList<EntityPlayer> entityPlayers = new ArrayList<>();

	public NameTags() {
		super("Nametags", "Thank you gishcode my beloved.");
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

	/*
	Thank you.
	Taken from https://github.com/GishReloaded/Gish-Code-1.12.2/blob/master/src/main/java/i/gishreloaded/gishcode/hack/hacks/Profiler.java
	 */

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		for (Object object : mc.world.loadedEntityList) {
			if(object instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase)object;
				RenderManager renderManager = mc.getRenderManager();
				double renderPosX = renderManager.viewerPosX;
				double renderPosY = renderManager.viewerPosY;
				double renderPosZ = renderManager.viewerPosZ;
				double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks()) - renderPosX;
				double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks()) - renderPosY;
				double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks()) - renderPosZ;

				this.renderNameTag(entity, entity.getName(), xPos, yPos, zPos);
			}
		}
	}

	void renderNameTag(EntityLivingBase entity, String tag, double x, double y, double z) {
		if(entity instanceof EntityArmorStand || entity == mc.player) {
			return;
		}

		int color = 130;
		EntityPlayerSP player = mc.player;
		FontRenderer fontRenderer = mc.fontRenderer;
		if(player.canEntityBeSeen(entity))
			color = 200;
		y += (entity.isSneaking() ? 0.5D : 0.7D);
		float distance = player.getDistance(entity) / 4.0F;
		if (distance < 1.6F) {
			distance = 1.6F;
		}

		if(entity instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer)entity;
			String ID = entityPlayer.getName();
		}

		int health = (int)entity.getHealth();
		if (health <= entity.getMaxHealth() * 0.25D) {
			tag = tag + "\u00a74";
		} else if (health <= entity.getMaxHealth() * 0.5D) {
			tag = tag + "\u00a76";
		} else if (health <= entity.getMaxHealth() * 0.75D) {
			tag = tag + "\u00a7e";
		} else if (health <= entity.getMaxHealth()) {
			tag = tag + "\u00a72";
		}
		tag = String.valueOf(tag) + " " + Math.round(health);

		RenderManager renderManager = mc.getRenderManager();
		float scale = distance;
		scale /= 30.0F;
		scale = (float)(scale * 0.3D);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y + 1.4F, (float)z);
		GL11.glNormal3f(1.0F, 1.0F, 1.0F);
		GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GL11.glDisable(2896);
		GL11.glDisable(2929);
		Tessellator var14 = Tessellator.getInstance();
		BufferBuilder var15 = var14.getBuffer();
		int width = fontRenderer.getStringWidth(tag) / 2;
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
//	     RenderUtils.drawRect(-width - 3.0F, -(fontRenderer.FONT_HEIGHT + 1) - 1.0F, width + 3.0F, -(fontRenderer.FONT_HEIGHT + 1), color);
//	     RenderUtils.drawRect(-width - 3.0F, 3.0F, width + 3.0F, 2.0F, color);
//	     RenderUtils.drawRect(-width - 3.0F, -(fontRenderer.FONT_HEIGHT + 1) - 1.0F, -width - 2, 3.0F, color);
//	     RenderUtils.drawRect(width + 3.0F, -(fontRenderer.FONT_HEIGHT + 1) - 1.0F, width + 2, 3.0F, color);
		fontRenderer.drawString(tag, -width - 2 + width + 2 - width + -fontRenderer.FONT_HEIGHT, Color.WHITE.getRGB(), color);
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer entityPlayer = (EntityPlayer)entity;
			GlStateManager.translate(0.0F, 1.0F, 0.0F);
			renderArmor(entityPlayer, 0, -(fontRenderer.FONT_HEIGHT + 1) - 20);
			GlStateManager.translate(0.0F, -1.0F, 0.0F);
		}
		GL11.glPushMatrix();
		GL11.glPopMatrix();
		GL11.glEnable(2896);
		GL11.glEnable(2929);
		GL11.glDisable(3042);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	public void renderArmor(EntityPlayer player, int x, int y) {
		InventoryPlayer items = player.inventory;
		ItemStack inHand = player.getHeldItemMainhand();
		ItemStack boots = items.armorItemInSlot(0);
		ItemStack leggings = items.armorItemInSlot(1);
		ItemStack body = items.armorItemInSlot(2);
		ItemStack helm = items.armorItemInSlot(3);
		ItemStack[] stuff = null;
		if (inHand != null) {
			stuff = new ItemStack[] { inHand, helm, body, leggings, boots };
		} else {
			stuff = new ItemStack[] { helm, body, leggings, boots };
		}
		ArrayList<ItemStack> stacks = new ArrayList<>();
		ItemStack[] array;
		int length = (array = stuff).length;

		for (int j = 0; j < length; j++)
		{
			ItemStack i = array[j];
			if ((i != null) && (i.getItem() != null)) {
				stacks.add(i);
			}
		}
		int width = 16 * stacks.size() / 2;
		x -= width;
		GlStateManager.disableDepth();
		for (ItemStack stack : stacks)
		{
			renderItem(stack, x, y);
			x += 16;
		}
		GlStateManager.enableDepth();
	}

	public void renderItem(ItemStack stack, int x, int y) {
		FontRenderer fontRenderer = mc.fontRenderer;
		RenderItem renderItem = mc.getRenderItem();
		EnchantEntry[] enchants = {
				new EnchantEntry(Enchantments.PROTECTION, "Pro"),
				new EnchantEntry(Enchantments.THORNS, "Th"),
				new EnchantEntry(Enchantments.SHARPNESS, "Shar"),
				new EnchantEntry(Enchantments.FIRE_ASPECT, "Fire"),
				new EnchantEntry(Enchantments.KNOCKBACK, "Kb"),
				new EnchantEntry(Enchantments.UNBREAKING, "Unb"),
				new EnchantEntry(Enchantments.POWER, "Pow"),
				new EnchantEntry(Enchantments.INFINITY, "Inf"),
				new EnchantEntry(Enchantments.PUNCH, "Punch")
		};
		GlStateManager.pushMatrix();
		GlStateManager.pushMatrix();
		float scale1 = 0.3F;
		GlStateManager.translate(x - 3, y + 10, 0.0F);
		GlStateManager.scale(0.3F, 0.3F, 0.3F);
		GlStateManager.popMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		renderItem.zLevel = -100.0F;
		GlStateManager.disableDepth();
		renderItem.renderItemIntoGUI(stack, x, y);
		renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
		GlStateManager.enableDepth();
		EnchantEntry[] array;
		int length = (array = enchants).length; for (int i = 0; i < length; i++) {
			EnchantEntry enchant = array[i];
			int level = EnchantmentHelper.getEnchantmentLevel(enchant.getEnchant(), stack);
			String levelDisplay = "" + level;
			if (level > 10) {
				levelDisplay = "10+";
			}
			if (level > 0) {
				float scale2 = 0.32F;
				GlStateManager.translate(x - 2, y + 1, 0.0F);
				GlStateManager.scale(0.42F, 0.42F, 0.42F);
				GlStateManager.disableDepth();
				GlStateManager.disableLighting();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				fontRenderer.drawString("\u00a7f" + enchant.getName() + " " + levelDisplay,
						20 - fontRenderer.getStringWidth("\u00a7f" + enchant.getName() + " " + levelDisplay) / 2, 0, Color.WHITE.getRGB(), true);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.enableLighting();
				GlStateManager.enableDepth();
				GlStateManager.scale(2.42F, 2.42F, 2.42F);
				GlStateManager.translate(-x, -y, 0.0F);
				y += (int)((fontRenderer.FONT_HEIGHT + 3) * 0.28F);
			}
		}
		renderItem.zLevel = 0.0F;
		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableAlpha();
		GlStateManager.disableBlend();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
	}

	public static class EnchantEntry {
		private Enchantment enchant;
		private String name;

		public EnchantEntry(Enchantment enchant, String name)
		{
			this.enchant = enchant;
			this.name = name;
		}

		public Enchantment getEnchant()
		{
			return this.enchant;
		}

		public String getName()
		{
			return this.name;
		}
	}
}
