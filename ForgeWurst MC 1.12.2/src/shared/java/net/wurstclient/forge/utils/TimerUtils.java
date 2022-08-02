package net.wurstclient.forge.utils;

public class TimerUtils{
    private static long current;

    public TimerUtils(){
        this.current = System.currentTimeMillis();
    }

    public static boolean hasReached(final long delay){
        return System.currentTimeMillis() - current >= delay;
    }

    public static boolean hasReached(final long delay, boolean reset){
        if (reset)
            reset();
        return System.currentTimeMillis() - current >= delay;
    }

    public static void reset(){
        current = System.currentTimeMillis();
    }

    public static long getTimePassed(){
        return System.currentTimeMillis() - current;
    }

    public static boolean sleep(final long time){
        if (time() >= time){
            reset();
            return true;
        }
        return false;
    }

    public static long time() {
        return System.currentTimeMillis() - current;
    }
}