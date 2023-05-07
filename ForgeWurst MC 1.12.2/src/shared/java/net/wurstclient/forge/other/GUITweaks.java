package net.wurstclient.forge.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.ForgeWurst;
import org.lwjgl.opengl.GL11;

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
}