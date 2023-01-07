package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;

public class NotiUtils {

    public static void render(String title, String description, boolean animate) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.setOverlayMessage("[" + title + "]" + " " + description, animate);
    }
}