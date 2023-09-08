/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge.hacks.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.BlockUtils;
import net.wurstclient.forge.utils.RenderUtils;
import org.lwjgl.opengl.GL11;

public final class Pointer extends Hack {

    private final SliderSetting x =
            new SliderSetting("X", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

    private final SliderSetting z =
            new SliderSetting("Z", 0, -32000000, 32000000, 1, SliderSetting.ValueDisplay.INTEGER);

    public Pointer() {
        super("Pointer", "Lets you save temporary points.");
        setCategory(Category.RENDER);
        addSetting(x);
        addSetting(z);
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
    public void onRenderWorldLast(RenderWorldLastEvent event) {
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

        GL11.glPopMatrix();

        // GL resets
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
}