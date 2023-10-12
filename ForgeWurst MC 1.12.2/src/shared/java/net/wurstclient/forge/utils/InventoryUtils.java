package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

public class InventoryUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void setSlot(int slot) {
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public static void click(int slot) {
        PlayerControllerUtils.windowClick_QUICK_MOVE(slot);
    }

    public static int getSlot(Item item) {
        for (int i = 0; i < 36; i ++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
