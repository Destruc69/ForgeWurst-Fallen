package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityUtils {

    public static int getFallDistance(Entity entity) {
        BlockPos pos = entity.getPosition();

        int c = 0;

        while (Minecraft.getMinecraft().world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
            pos = pos.add(0, -1, 0);
            c++;
        }

        return c;
    }
}
