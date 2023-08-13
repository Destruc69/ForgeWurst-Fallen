package net.wurstclient.forge.other.customs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.clickgui.ClickGuiScreen;
import org.lwjgl.opengl.GL11;

public class ClickGUIButtonMainMenu extends GuiButton  {

    public ClickGUIButtonMainMenu(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int regionX = this.x;
        int regionY = this.y;
        int regionWidth = 100;
        int regionHeight = 20;

        if (mouseX >= regionX && mouseX < regionX + regionWidth &&
                mouseY >= regionY && mouseY < regionY + regionHeight) {
            mc.renderGlobal.loadRenderers();
            mc.displayGuiScreen(new ClickGuiScreen(ForgeWurst.getForgeWurst().getGui()));
            return true;
        }

        return false;
    }
}
