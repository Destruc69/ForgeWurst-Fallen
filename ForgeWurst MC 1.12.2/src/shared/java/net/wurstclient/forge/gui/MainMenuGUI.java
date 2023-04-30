package net.wurstclient.forge.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainMenuGUI {

    @SubscribeEvent
    public void tick(GuiScreenEvent.InitGuiEvent event) {
        try {
            GuiMultiplayer guiMultiplayer = (GuiMultiplayer) event.getGui();

            //Faster server pinging
            guiMultiplayer.getOldServerPinger().pingPendingNetworks();

            //Watermark for Multiplayer
            guiMultiplayer.getServerList().getServerData(0).serverMOTD = "FallenUtilityMod on Top!";
        } catch (Exception ignored) {
        }
    }
}