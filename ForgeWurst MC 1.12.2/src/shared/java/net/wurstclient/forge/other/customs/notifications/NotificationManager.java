package net.wurstclient.forge.other.customs.notifications;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.IngameHUD;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.hacks.ClickGuiHack;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<Notification> notificationArrayList;

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {

        if (ClickGuiHack.addDummyNotification.isChecked()) {
            NotificationManager.addNotification(new Notification("DUMMYNOTIFICATIONDUMMYNOTIFICATION"));
            ClickGuiHack.addDummyNotification.setChecked(false);
        }

        if (notificationArrayList.size() > 0) {
            if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo)
                return;

            int y = ClickGuiHack.notificationY.getValueI();
            Iterator<Notification> iterator = notificationArrayList.iterator();

            while (iterator.hasNext()) {
                Notification notification = iterator.next();

                if (notification.getTicksExisted() < 555) {
                    notification.tick();
                } else {
                    iterator.remove(); // Safely remove the element from the list.
                }

                WMinecraft.getFontRenderer().drawString(notification.getContent() + "   [" + notification.getTicksExisted() % 20 + "]", ClickGuiHack.notificationX.getValueI(), y, (int) IngameHUD.textColor, false);

                y += 9;
            }
        }
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
