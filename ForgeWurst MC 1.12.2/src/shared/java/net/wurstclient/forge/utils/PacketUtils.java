package net.wurstclient.forge.utils;

import net.minecraft.network.play.client.CPacketPlayer;

public class PacketUtils {

    public static boolean changesPosition(CPacketPlayer packet) {
        double xDiff = packet.getX(0) - packet.getX(0);
        double yDiff = packet.getY(0) - packet.getY(0);
        double zDiff = packet.getZ(0) - packet.getZ(0);
        return (xDiff != 0.0 || yDiff != 0.0 || zDiff != 0.0);
    }

    public static boolean changesLook(CPacketPlayer packet) {
        float yawDiff = packet.getYaw(0) - packet.getYaw(0);
        float pitchDiff = packet.getPitch(0) - packet.getPitch(0);
        return (yawDiff != 0.0f || pitchDiff != 0.0f);
    }
}
