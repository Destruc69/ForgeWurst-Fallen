package net.wurstclient.forge.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SystemUtils {

    public static double getUsedMemoryPercentage() {
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();

        // Get the total amount of free memory available to the JVM
        long freeMemory = runtime.freeMemory();

        // Get the total memory currently available to the JVM
        long totalMemory = runtime.totalMemory();

        // Calculate used memory percentage
        double usedMemoryPercentage = ((double) (totalMemory - freeMemory) / totalMemory) * 100;

        return usedMemoryPercentage;
    }
}
