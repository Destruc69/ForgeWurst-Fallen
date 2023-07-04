package net.wurstclient.forge.other.customs;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class UnlimitedTextField extends GuiTextField {
    private FontRenderer renderer;
    public UnlimitedTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);
        renderer = fontrendererObj;
        this.setMaxStringLength(999);
    }
}