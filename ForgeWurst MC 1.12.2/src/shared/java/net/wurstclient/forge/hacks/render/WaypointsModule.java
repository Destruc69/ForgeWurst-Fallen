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
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.commands.WaypointsCmd;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.waypoints.Waypoint;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class WaypointsModule extends Hack {

    public WaypointsModule() {
        super("Waypoints", "Lets you save locations, with handy line to guide you.");
        setCategory(Category.RENDER);
    }

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);

        if (ForgeWurst.getForgeWurst().getWaypoints().getVectors().size() <= 0) {
            ChatUtils.message("To add waypoints, use the .waypoints command.");
        }
    }

    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (ForgeWurst.getForgeWurst().getWaypoints().size() > 0) {
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

                for (Waypoint waypoint : getVectors()) {
                    drawNametags(String.valueOf(Math.round(mc.player.getDistance(WaypointsCmd.decode(waypoint.getX()), mc.player.lastTickPosY, WaypointsCmd.decode(waypoint.getZ())))) + " x" + WaypointsCmd.decode(Math.round(waypoint.getX())) + " " + "z" + WaypointsCmd.decode(Math.round(waypoint.getZ())), WaypointsCmd.decode(waypoint.getX()), mc.player.lastTickPosY, WaypointsCmd.decode(waypoint.getZ()));
                }

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
    }

    public void drawNametags(String content, double x, double y, double z) {
        BlockPos blockPos = getClosestSolidBlock(new BlockPos(x, y, z));
        x = blockPos.getX();
        z = blockPos.getZ();
        try {
            float distance = (float) mc.player.getDistance(x, y, z);
            float var13 = (distance / 5 <= 2 ? 2.0F : distance / 5) * 0.7F;
            float var14 = 0.044444446F * var13;
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

    public List<Waypoint> getVectors() {
        return new ArrayList<>(ForgeWurst.getForgeWurst().getWaypoints().getVectors());
    }

    private BlockPos getClosestSolidBlock(BlockPos targetPos) {
        int renderDistanceChunks = mc.gameSettings.renderDistanceChunks;

        assert targetPos != null;

        double closestDistance = Double.MAX_VALUE;
        BlockPos closestBlock = null;
        for (int x = mc.player.chunkCoordX - renderDistanceChunks; x <= mc.player.chunkCoordX + renderDistanceChunks; x++) {
            for (int z = mc.player.chunkCoordZ - renderDistanceChunks; z <= mc.player.chunkCoordZ + renderDistanceChunks; z++) {
                for (int y = 0; y <= 256; y++) {
                    BlockPos blockPos = new BlockPos(x * 16, y, z * 16);
                    double distance = blockPos.distanceSq(targetPos);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestBlock = blockPos;
                    }
                }
            }
        }
        assert closestBlock != null;
        return closestBlock;
    }
}