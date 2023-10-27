package net.wurstclient.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;

import java.util.ArrayList;
import java.util.Objects;

public class FriendProccessor {
    private static final FriendsList friendsList = ForgeWurst.getForgeWurst().getFriendsList();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<EntityPlayer> friendsOnline = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(WUpdateEvent event) {
        try {
            for (EntityPlayer entityPlayer : Objects.requireNonNull(mc.player.getServer()).getPlayerList().getPlayers()) {
                for (int i = 0; i < friendsList.size() - 1; i++) {
                    if (friendsList.get(i).getName().equalsIgnoreCase(entityPlayer.getName().toLowerCase())) {
                        if (!friendsOnline.contains(entityPlayer)) {
                            friendsOnline.add(entityPlayer);
                        }
                    }
                }
            }

            friendsOnline.removeIf(player -> !mc.player.getServer().getPlayerList().getPlayers().contains(player));
        } catch (Exception ignored) {
        }
    }
}
