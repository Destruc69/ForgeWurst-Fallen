package net.wurstclient.forge.other.customs.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.ClickGuiHack;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<Notification> notificationArrayList;

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (ClickGuiHack.animateNotifications.isChecked()) {
            if (notificationArrayList.size() > 0) {
                if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo)
                    return;

                ScaledResolution scaledResolution = new ScaledResolution(mc);

                int y = ClickGuiHack.notificationY.getValueI();
                Iterator<Notification> iterator = notificationArrayList.iterator();

                while (iterator.hasNext()) {
                    Notification notification = iterator.next();
                    int animationDuration = 800; // You can adjust this value for smoother or faster animations

                    // Calculate the animation progress
                    float progress = (float) notification.getTicksExisted() / animationDuration;
                    int animationOffset = (int) (scaledResolution.getScaledWidth() * progress);

                    // Check if the notification's duration has not expired
                    if (notification.getTicksExisted() < animationDuration) {
                        notification.tick();
                    } else {
                        iterator.remove(); // Safely remove the expired notification from the list.
                    }

                    // Draw the notification content with animation offset
                    int xPosition = ClickGuiHack.notificationX.getValueI() + animationOffset;

                    // Draw only if entering or leaving
                    if (progress > 0 && progress < 1) {
                        mc.fontRenderer.drawString(notification.getContent(), xPosition, y, 0xFFFFFFFF, false);
                    }

                    y += 14; // Adjust the vertical spacing between notifications
                }
            }
        } else {
            if (notificationArrayList.size() > 0) {
                // Check if the event type is appropriate for rendering notifications
                if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.gameSettings.showDebugInfo)
                    return;

                int y = ClickGuiHack.notificationY.getValueI();
                Iterator<Notification> iterator = notificationArrayList.iterator();

                while (iterator.hasNext()) {
                    Notification notification = iterator.next();

                    // Check if the notification's duration has not expired
                    if (notification.getTicksExisted() < 500) {
                        notification.tick();
                    } else {
                        iterator.remove(); // Safely remove the expired notification from the list.
                    }

                    // Draw the notification content
                    mc.fontRenderer.drawString(notification.getContent(), ClickGuiHack.notificationX.getValueI(), y, 0xFFFFFFFF, false);

                    y += 14; // Adjust the vertical spacing between notifications
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        for (Hack hack : ForgeWurst.getForgeWurst().getHax().getValues()) {
            boolean isEnabled = hack.isEnabled();
            boolean wasEnabled = hack.getPrevState(); // Get the previous state

            if (isEnabled != wasEnabled) {
                // Status changed
                if (isEnabled) {
                    // Code to execute when enabled
                    notificationArrayList.add(new Notification(hack.getName() + " enabled"));
                } else {
                    // Code to execute when disabled
                    notificationArrayList.add(new Notification(hack.getName() + " disabled"));
                }
            }

            // Update the previous state
            hack.setPrevState(isEnabled); // Assuming there's a setPrevState() method to update the previous state
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