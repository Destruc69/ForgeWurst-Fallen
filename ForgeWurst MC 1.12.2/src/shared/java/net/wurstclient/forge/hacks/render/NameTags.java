package net.wurstclient.forge.hacks.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class NameTags extends Hack {

	private final SliderSetting scale =
			new SliderSetting("Scale", 2, 0.1, 10, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	public NameTags() {
		super("Nametags", "Renders useful information about the player above the player");
		setCategory(Category.RENDER);
		addSetting(scale);
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
		for (Entity entity : mc.world.loadedEntityList) {
			if (entity.isEntityAlive() && entity != mc.player && entity instanceof EntityPlayer) {
				// GL settings
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glLineWidth(1);
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDisable(GL11.GL_DEPTH_TEST);

				GL11.glPushMatrix();
				GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
						-TileEntityRendererDispatcher.staticPlayerY,
						-TileEntityRendererDispatcher.staticPlayerZ);

				renderNametags(event.getPartialTicks(), (EntityPlayer) entity);

				GL11.glPopMatrix();

				// GL resets
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);
			}
		}
	}

	private void renderNametags(double partialTicks, EntityPlayer e) {
		GL11.glPushMatrix();
		GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
				e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);

		drawNameplate(e, (float) partialTicks);

		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
	}

	public void drawNameplate(EntityPlayer player, float partialTicks) {
		if (mc.getRenderManager().options == null || player.isDead || player.getHealth() + player.getAbsorptionAmount() == 0) {
			return;
		}

		boolean isThirdPersonFrontal = mc.getRenderManager().options.thirdPersonView == 2;
		double scale = this.scale.getValue();
		double playerX = (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX);
		double playerY = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY);
		double playerZ = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ);
		float viewerYaw = mc.getRenderManager().playerViewY;
		float viewerPitch = mc.getRenderManager().playerViewX;
		playerY += 2.1;

		GlStateManager.pushMatrix();
		GlStateManager.translate(playerX, playerY, playerZ);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-scale, -scale, -scale);
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		//Name
		String name = StringUtils.stripControlCodes(player.getName());

		//Health
		String playerName = name;
		int health = (int) (player.getHealth() + player.getAbsorptionAmount());
		ChatFormatting color2 = ChatFormatting.RED;
		if (health >= 20) {
			color2 = ChatFormatting.GREEN;
		} else if (health >= 14) {
			color2 = ChatFormatting.YELLOW;
		} else if (health >= 7) {
			color2 = ChatFormatting.GOLD;
		}

		playerName += color2 + " " + health;

		mc.fontRenderer.drawStringWithShadow(playerName, -mc.fontRenderer.getStringWidth(playerName) / 2, -9, -1);

		boolean renderedArmor = false;
		Iterator<ItemStack> items = player.getArmorInventoryList().iterator();
		ArrayList<ItemStack> stacks = new ArrayList<>();

		stacks.add(player.getHeldItemOffhand());

		while (items.hasNext()) {
			final ItemStack stack = items.next();
			if (stack != null && stack.getItem() != Items.AIR) {
				stacks.add(stack);
			}
		}

		stacks.add(player.getHeldItemMainhand());
		Collections.reverse(stacks);
		int x = 0;

		for (ItemStack stack : stacks) {
			if (stack != null) {
				Item item = stack.getItem();
				if (item != Items.AIR) {
					//Render items and armor
					if (item instanceof ItemBlock) {
						GlStateManager.pushMatrix();
						GlStateManager.enableBlend();
						GlStateManager.disableDepth();
						RenderHelper.enableStandardItemLighting();
					} else {
						GlStateManager.pushMatrix();
						GlStateManager.depthMask((boolean) true);
						GlStateManager.clear((int) 256);
						RenderHelper.enableStandardItemLighting();
						mc.getRenderItem().zLevel = -150.0f;
						GlStateManager.disableAlpha();
						GlStateManager.enableDepth();
						GlStateManager.disableCull();
					}
					//Oh man it took like forever to figure out this right scale for this.
					//It took more than time. Even a few brain cells were lost
					double itemScale = (double) mc.fontRenderer.FONT_HEIGHT / (double) 9;
					GlStateManager.scale(itemScale, itemScale, 0);
					GlStateManager.translate(x - (16 * stacks.size() / 2), -mc.fontRenderer.FONT_HEIGHT - 23, 0);

					mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
					mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, 0, 0);

					if (item instanceof ItemBlock) {
						RenderHelper.disableStandardItemLighting();
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					} else {
						mc.getRenderItem().zLevel = 0.0f;
						RenderHelper.disableStandardItemLighting();
						GlStateManager.enableCull();
						GlStateManager.enableAlpha();
						GlStateManager.disableDepth();
						GlStateManager.enableDepth();
						GlStateManager.popMatrix();
					}

					x += 16;
					renderedArmor = true;
				}


				/*
				if (stack.getMaxDamage() != 0) {
					String string = "" + stack.getItem().getDurabilityForDisplay(new ItemStack(item));
					GlStateManager.pushMatrix();
					GlStateManager.disableDepth();
					GlStateManager.translate(x - ((16.0f * stacks.size()) / 2.0f) - (16.0f / 2.0f) - (mc.fontRenderer.getStringWidth(string) / 4.0f), -mc.fontRenderer.FONT_HEIGHT - 26, 0);
					GlStateManager.scale(0.5f, 0.5f, 0.5f);
					mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
					GlStateManager.scale(2, 2, 2);
					GlStateManager.enableDepth();
					GlStateManager.popMatrix();
				}
				 */
			}
		}

		if (player.getHeldItemMainhand().getItem() != Items.AIR) {
			int y = 31;
			if (!renderedArmor)  {
				y = 5;
			}

			String string = player.getHeldItemMainhand().getDisplayName();
			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.translate(-(mc.fontRenderer.getStringWidth(string) / 4.0f), -mc.fontRenderer.FONT_HEIGHT - y, 0);
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			mc.fontRenderer.drawStringWithShadow(string, 0, 0, -1);
			GlStateManager.scale(2, 2, 2);
			GlStateManager.enableDepth();
			GlStateManager.popMatrix();
		}

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}