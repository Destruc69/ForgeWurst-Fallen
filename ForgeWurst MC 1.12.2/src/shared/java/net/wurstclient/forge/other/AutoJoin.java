package net.wurstclient.forge.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.wurstclient.forge.hacks.player.AutoJoinModule;
import org.lwjgl.Sys;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;

public class AutoJoin {

    private boolean isAutoJoining = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && isAutoJoining) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) == AutoJoinModule.timeToEngage.getValueI()) {
                GuiMultiplayer guiMultiplayer = (GuiMultiplayer) Minecraft.getMinecraft().currentScreen;
                if (guiMultiplayer != null) {
                    guiMultiplayer.selectServer(AutoJoinModule.indexToJoin.getValueI());
                    guiMultiplayer.connectToSelected();
                }
                // Set auto-joining to false to prevent repeated attempts during the same hour
                isAutoJoining = false;
            }
        }
    }

    @SubscribeEvent
    public void onRender(GuiScreenEvent.InitGuiEvent.Post event) {
        // Set auto-joining to true when entering the multiplayer menu
        isAutoJoining = AutoJoinModule.enable.isChecked() && event.getGui() instanceof GuiMultiplayer;
    }

    @SubscribeEvent
    public void onGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (AutoJoinModule.enable.isChecked()) {
            String text = "AutoJoin is Engaged! Will join server index " + AutoJoinModule.indexToJoin.getValueString() + " at hour " + AutoJoinModule.timeToEngage.getValueString();
            int textX = 5;
            int textY = 5;

            mc.fontRenderer.drawStringWithShadow(text, textX, textY, 0xFFFFFF);
            mc.fontRenderer.drawStringWithShadow("Countdown: " + getTimeUntilNextTargetHour(AutoJoinModule.timeToEngage.getValueI()), 5, 20, 0xFFFFFF);
        }
    }

    public static String getTimeUntilNextTargetHour(int targetHour) {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Create a LocalDateTime object for the next occurrence of the target hour
        LocalDateTime nextTargetDateTime = currentDateTime.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);

        if (currentDateTime.compareTo(nextTargetDateTime) >= 0) {
            // If the current time is after or equal to the target hour, add one day to the current time
            nextTargetDateTime = nextTargetDateTime.plusDays(1);
        }

        // Calculate the duration between current time and the next target time
        Duration duration = Duration.between(currentDateTime, nextTargetDateTime);

        // Extract hours, minutes, and seconds from the duration
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return hours + " : " + minutes + " : " + seconds;
    }
}