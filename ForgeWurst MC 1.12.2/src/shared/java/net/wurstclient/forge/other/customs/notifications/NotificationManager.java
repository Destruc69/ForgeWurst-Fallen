package net.wurstclient.forge.other.customs.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.hacks.ClickGuiHack;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<Notification> notificationArrayList;

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        // Check if the "addDummyNotification" option is enabled
        if (ClickGuiHack.addDummyNotification.isChecked()) {
            NotificationManager.addNotification(new Notification("DUMMYNOTIFICATIONDUMMYNOTIFICATION"));
            ClickGuiHack.addDummyNotification.setChecked(false);
        }

        if (notificationArrayList.size() > 0) {
            // Check if the event type is appropriate for rendering notifications
            if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo)
                return;

            int y = ClickGuiHack.notificationY.getValueI();
            Iterator<Notification> iterator = notificationArrayList.iterator();

            while (iterator.hasNext()) {
                Notification notification = iterator.next();

                // Check if the notification's duration has not expired
                if (notification.getTicksExisted() < 555) {
                    notification.tick();
                } else {
                    iterator.remove(); // Safely remove the expired notification from the list.
                }

                if (ClickGuiHack.blockArrayList.isChecked()) {
                    // Draw a bordered box behind the notification content
                    drawBorderedRect(ClickGuiHack.notificationX.getValueI() - 2, y - 2, mc.fontRenderer.getStringWidth(notification.getContent()) + 4, 12, 1, 0xFF000000, 0x55000000);
                }

                // Draw the notification content
                mc.fontRenderer.drawString(notification.getContent(), ClickGuiHack.notificationX.getValueI(), y, 0xFFFFFFFF, false);

                y += 14; // Adjust the vertical spacing between notifications
            }
        }
    }

    // Helper method to draw a bordered rectangle
    private void drawBorderedRect(int x, int y, int width, int height, int borderWidth, int borderColor, int boxColor) {
        Gui.drawRect(x, y, x + width, y + height, boxColor);
        Gui.drawRect(x - borderWidth, y - borderWidth, x + width + borderWidth, y + height + borderWidth, borderColor);
    }

    public static void addNotification(Notification notification) {
        notificationArrayList.add(notification);
    }

    public static void removeNotification(Notification notification) {
        notificationArrayList.remove(notification);
    }

    public static ArrayList<Notification> getNotificationArrayList()  {
        return notificationArrayList;
    }
}
