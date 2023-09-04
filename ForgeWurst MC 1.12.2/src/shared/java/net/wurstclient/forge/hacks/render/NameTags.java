package net.wurstclient.forge.hacks.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.SliderSetting;
import org.lwjgl.opengl.GL11;

public final class NameTags extends Hack {

	private final SliderSetting red =
			new SliderSetting("Red", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting green =
			new SliderSetting("Green", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting blue =
			new SliderSetting("Blue", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	private final SliderSetting alpha =
			new SliderSetting("Alpha", 1, 0, 1, 0.1, SliderSetting.ValueDisplay.DECIMAL);

	public NameTags() {
		super("Nametags", "Renders nametags above the players head.");
		setCategory(Category.RENDER);
		addSetting(red);
		addSetting(green);
		addSetting(blue);
		addSetting(alpha);
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
			if (entity.isEntityAlive()) {
				// GL settings
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glLineWidth(2);

				GL11.glPushMatrix();
				GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
						-TileEntityRendererDispatcher.staticPlayerY,
						-TileEntityRendererDispatcher.staticPlayerZ);

				double partialTicks = event.getPartialTicks();

				renderNametags(partialTicks, entity);

				GL11.glPopMatrix();

				// GL resets
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);
			}
		}
	}

	private void renderNametags(double partialTicks, Entity e) {
		GL11.glPushMatrix();
		GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
				e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
				e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);

		drawNameplate(WMinecraft.getFontRenderer(),
				e.getName(),
				0, 2.5f, 0, 0, mc.getRenderManager().playerViewY,
				mc.getRenderManager().playerViewX,
				mc.getRenderManager().options.thirdPersonView == 2, e.isSneaking());
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
	}

	private void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.050F, -0.050F, 0.050F);
		GlStateManager.disableLighting();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		int i = fontRendererIn.getStringWidth(str) / 2;

		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) (-i - 1), (double) (-1 + verticalShift) + 0.5, 0.0D).color(red.getValueF(), green.getValueF(), blue.getValueF(), alpha.getValueF()).endVertex();
		bufferbuilder.pos((double) (-i - 1), (double) (8 + verticalShift) + 0.5, 0.0D).color(red.getValueF(), green.getValueF(), blue.getValueF(), alpha.getValueF()).endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (8 + verticalShift) + 0.5, 0.0D).color(red.getValueF(), green.getValueF(), blue.getValueF(), alpha.getValueF()).endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (-1 + verticalShift) + 0.5, 0.0D).color(red.getValueF(), green.getValueF(), blue.getValueF(), alpha.getValueF()).endVertex();

		tessellator.draw();
		GlStateManager.enableTexture2D();

		fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
		GlStateManager.enableDepth();

		fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}