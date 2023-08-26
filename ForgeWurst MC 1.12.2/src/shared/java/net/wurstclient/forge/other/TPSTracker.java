package net.wurstclient.forge.other;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TPSTracker {

    private static int tickCounter = 0;
    private static long lastTime = System.nanoTime();
    private static final double[] tickTimeArray = new double[10]; // Store last 10 tick times
    private static double tps = 20.0; // Default to 20 TPS

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            long currentTime = System.nanoTime();
            double tickTime = (currentTime - lastTime) / 1000000.0; // Convert to milliseconds
            lastTime = currentTime;

            tickTimeArray[tickCounter % tickTimeArray.length] = tickTime;
            tickCounter++;

            if (tickCounter % tickTimeArray.length == 0) {
                double totalTickTime = 0.0;
                for (double time : tickTimeArray) {
                    totalTickTime += time;
                }
                tps = 1000.0 / (totalTickTime / tickTimeArray.length);
            }
        }
    }

    public static double getCurrentTPS() {
        return tps;
    }
}