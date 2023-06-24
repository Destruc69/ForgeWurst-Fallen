package net.wurstclient.forge.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.other.customs.ClickGUIButtonMainMenu;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GUITweaks {
    @SubscribeEvent
    public void onGUI(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu) {
            Minecraft mc = Minecraft.getMinecraft();

            GL11.glColor4f(1, 1, 1, 1);

            // Draw Wurst logo at top left
            ResourceLocation resourceLocation1 = new ResourceLocation(ForgeWurst.MODID, "wurst-logo.png");
            mc.getTextureManager().bindTexture(resourceLocation1);

            ScaledResolution sr = new ScaledResolution(mc);
            int logoWidth1 = 64 * sr.getScaleFactor();
            int logoHeight1 = 32 * sr.getScaleFactor();

            int logoX = 10;
            int logoY = 10;

            Gui.drawModalRectWithCustomSizedTexture(logoX, logoY, 0, 0, logoWidth1, logoHeight1, logoWidth1, logoHeight1);

            // Draw "FORGEWURST" text under Wurst logo
            String text = "FORGEWURST";
            int textWidth = mc.fontRenderer.getStringWidth(text);
            int textX = logoX + (logoWidth1 - textWidth) / 2;
            int textY = logoY + logoHeight1 + 5;

            mc.fontRenderer.drawStringWithShadow(text, textX, textY, 0xFFFFFF);

            // Draw Fallen logo at top right
            ResourceLocation resourceLocation2 = new ResourceLocation(ForgeWurst.MODID, "fallen-logo.png");
            mc.getTextureManager().bindTexture(resourceLocation2);

            int logoWidth2 = 64 * sr.getScaleFactor();
            int logoHeight2 = 32 * sr.getScaleFactor();

            int logo2X = sr.getScaledWidth() - logoWidth2 - 10;
            int logo2Y = 10;

            Gui.drawModalRectWithCustomSizedTexture(logo2X, logo2Y, 0, 0, logoWidth2, logoHeight2, logoWidth2, logoHeight2);
        }
    }

    @SubscribeEvent
    public void onGUI(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            int buttonWidth = 100;
            int buttonHeight = 20;
            int buttonMargin = 5; // Margin between buttons

            List<GuiButton> buttonList = event.getButtonList();
            int numButtons = buttonList.size();

            int buttonX = event.getGui().width / 2 - (buttonWidth / 2); // Center the button horizontally
            int buttonY = event.getGui().height / 2 + (numButtons * (buttonHeight + buttonMargin)) - 140; // Position the button below the existing buttons with a margin

            GuiButton guiButton = new ClickGUIButtonMainMenu(numButtons + 1, buttonX, buttonY, buttonWidth, buttonHeight, "ClickGUI");

            guiButton.packedFGColour = 0xadd8e6;

            event.getButtonList().add(guiButton);
        }
    }
}