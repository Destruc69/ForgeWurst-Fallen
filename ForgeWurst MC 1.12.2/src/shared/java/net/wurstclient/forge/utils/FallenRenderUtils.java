package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class FallenRenderUtils {
    public static void renderPosFilled(BlockPos blockPos, double partialticks, float red, float blue, float green, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        AxisAlignedBB alignedBB = BlockUtils.getBoundingBox(blockPos);
        // your positions. You might want to shift them a bit too
        int sX = (int)mc.player.posX;
        int sY = (int)mc.player.posY;
        int sZ = (int)mc.player.posZ;
// Usually the player
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
//Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
        assert entity != null;
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialticks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialticks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialticks;
//Apply 0-our transforms to set everything back to 0,0,0
        Tessellator.getInstance().getBuffer().setTranslation(-d0, -d1, -d2);
//Your render function which renders boxes at a desired position. In this example I just copy-pasted the one on TileEntityStructureRenderer
        assert alignedBB != null;
        RenderGlobal.renderFilledBox(alignedBB, red, green, blue, alpha);
//When you are done rendering all your boxes reset the offsets. We do not want everything that renders next to still be at 0,0,0 :)
        Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);
    }
}
