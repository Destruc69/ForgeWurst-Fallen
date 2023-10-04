package net.wurstclient.forge.other.customs.notifications;

public class Notification {

    private String content;
    private int ticksExisted;

    public Notification(String content) {
        this.content = content;
        ticksExisted = 0;
    }

    public String getContent() {
        return this.content;
    }

    public int getTicksExisted() {
        return this.ticksExisted;
    }

    public void tick() {
        ticksExisted++;
    }
}
