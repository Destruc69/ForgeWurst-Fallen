package net.wurstclient.forge.hacks.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import org.lwjgl.opengl.GL11;

import java.util.Formatter;

public final class NameTags extends Hack {

	public NameTags() {
		super("Nametags", "Renders useful information about the player above the player");
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
	public void onRender(RenderWorldLastEvent event) {
		for (Entity entity : mc.world.playerEntities) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				float partialTicks = event.getPartialTicks();
				double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
				double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
				double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
				drawNametags(player, x, y, z);
			}
		}
	}

	public void drawNametags(EntityLivingBase entity, double x, double y, double z) {

		String entityName = entity.getDisplayName().getFormattedText();
		if (entity == mc.player)
			return;

		double health = entity.getHealth() / 2;
		double maxHealth = entity.getMaxHealth() / 2;
		double percentage = 100 * (health / maxHealth);
		String healthColor;

		if (percentage > 75) healthColor = "a";
		else if (percentage > 50) healthColor = "e";
		else if (percentage > 25) healthColor = "4";
		else healthColor = "4";

		Formatter formatter = new Formatter();
		String healthDisplay = String.valueOf(formatter.format(String.valueOf(Math.floor((health + (double) 0.5F / 2) / 0.5F) * 0.5F)));

		entityName = String.format("  %s \247%s%s ", entityName, healthColor, healthDisplay);

		float distance = mc.player.getDistance(entity);
		float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 0.7F;
		float var14 = 0.016666668F * var13;
		GlStateManager.pushMatrix();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.translate(x + 0.0F, y + entity.height + 0.4F, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		if (mc.gameSettings.thirdPersonView == 2) {
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.getRenderManager().playerViewX, -1.0F, 0.0F, 0.0F);
		} else {
			GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
		}
		GlStateManager.scale(-var14, -var14, var14);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();
		int var17 = 0;
		if (entity.isSneaking()) {
			var17 += 4;
		}
		var17 -= distance / 5;
		if (var17 < -8) {
			var17 = -8;
		}
		GlStateManager.disableTexture2D();
		float var18 = mc.fontRenderer.getStringWidth(entityName) / 2;
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldRenderer.pos((double) (-var18 + 3), (double) (-3 + var17), 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
		worldRenderer.pos((double) (-var18 + 3), (double) (9 + var17), 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
		worldRenderer.pos((double) (var18 - 1), (double) (8 + var17), 0.0D).color(0.0F, 0.0F, 0.0F, 0.5F).endVertex();
		worldRenderer.pos((double) (var18 - 1), (double) (-3 + var17), 0.0D).color(0.0F, 0.0F, 0.0F, 0.2F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		mc.fontRenderer.drawStringWithShadow(entityName, -var18, var17 - 1,0xFFFFFFFF);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}
}