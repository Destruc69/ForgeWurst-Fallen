package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public class FallenRenderUtils {
    public static void renderPosFilled(BlockPos blockPos, double partialticks, float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity entity = mc.getRenderViewEntity();
        assert entity != null;
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialticks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialticks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialticks;
        RenderGlobal.renderFilledBox(Objects.requireNonNull(BlockUtils.getBoundingBox(blockPos)).offset(-d0, -d1, -d2), red, green, blue, alpha);
    }

    public static void renderPosOutline(BlockPos blockPos, double partialticks, float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity entity = mc.getRenderViewEntity();
        assert entity != null;
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialticks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialticks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialticks;
        RenderGlobal.drawSelectionBoundingBox(Objects.requireNonNull(BlockUtils.getBoundingBox(blockPos)).offset(-d0, -d1, -d2), red, green, blue, alpha);
    }


    public static void drawLine(Vec3d posA, Vec3d posB, float width, Color c) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.translate(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        double dx = posB.x - posA.x;
        double dy = posB.y - posA.y;
        double dz = posB.z - posA.z;

        bufferBuilder.pos(posA.x, posA.y, posA.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
        bufferBuilder.pos(posA.x + dx, posA.y + dy, posA.z + dz).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

        tessellator.draw();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
