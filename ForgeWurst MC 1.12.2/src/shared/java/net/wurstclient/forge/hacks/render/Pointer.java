/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.pathfinding.PathfinderAStar;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public final class Pointer extends Hack {

    private final SliderSetting x =
            new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

    private final SliderSetting z =
            new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

    private final CheckboxSetting setToCurrentPos =
            new CheckboxSetting("SetToCurrentPos", "Sets the X and Z slider to the players current coordinate.",
                    false);

    public Pointer() {
        super("Pointer", "Lets you save temporary points.");
        setCategory(Category.RENDER);
        addSetting(x);
        addSetting(z);
        addSetting(setToCurrentPos);
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
        if (setToCurrentPos.isChecked()) {
            x.setValue(mc.player.lastTickPosX);
            z.setValue(mc.player.lastTickPosZ);
            setToCurrentPos.setChecked(false);
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            // GL settings
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(2);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glPushMatrix();
            GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX,
                    -TileEntityRendererDispatcher.staticPlayerY,
                    -TileEntityRendererDispatcher.staticPlayerZ);

            GL11.glColor4f(0, 1, 0, 1F);
            GL11.glBegin(GL11.GL_LINE);
            RenderUtils.drawArrow(new Vec3d(x.getValue(), 0, z.getValue()), new Vec3d(x.getValue(), 255, z.getValue()));
            GL11.glEnd();

            GL11.glColor4f(0, 1, 0, 1F);
            GL11.glBegin(GL11.GL_LINE);
            RenderUtils.drawArrow(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY - 1, mc.player.lastTickPosZ), new Vec3d(x.getValue(), mc.player.lastTickPosY - 1, z.getValue()));
            GL11.glEnd();

            drawNametags(String.valueOf(Math.round(mc.player.getDistance(x.getValue(), mc.player.lastTickPosY, z.getValue()))), PathfinderAStar.getClosestSolidBlock(new BlockPos(x.getValue(), mc.player.lastTickPosY, z.getValue())).getX(), mc.player.lastTickPosY, PathfinderAStar.getClosestSolidBlock(new BlockPos(x.getValue(), mc.player.lastTickPosY, z.getValue())).getZ());

            GL11.glPopMatrix();

            // GL resets
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        } catch (Exception ignored) {
        }
    }

    public void drawNametags(String content, double x, double y, double z) {
        try {
            float distance = (float) mc.player.getDistance(x, y, z);
            float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 0.7F;
            float var14 = 0.066666668F * var13;
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.translate(x, y, z);
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
            int var17 = 0;
            var17 -= distance / 5;
            if (var17 < -8) {
                var17 = -8;
            }
            GlStateManager.disableTexture2D();
            float var18 = mc.fontRenderer.getStringWidth(content);
            GlStateManager.enableTexture2D();
            mc.fontRenderer.drawStringWithShadow(content, -var18, var17 - 1, 0xFFFFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        } catch (Exception ignored) {
        }
    }
}