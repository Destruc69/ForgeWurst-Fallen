package net.wurstclient.forge.utils;

import net.minecraft.client.Minecraft;
import net.wurstclient.forge.other.customs.UnlimitedTextField;

public class GUIUtils {
    public static void renderTextBoxForLabel(String string, int x, int y, int width, int height, int textColor) {
        UnlimitedTextField unlimitedTextField = new UnlimitedTextField(80, Minecraft.getMinecraft().fontRenderer, x, y, width, height);
        unlimitedTextField.setText(string);
        unlimitedTextField.setVisible(true);
        unlimitedTextField.setEnabled(false);
        unlimitedTextField.setEnableBackgroundDrawing(true);
        unlimitedTextField.drawTextBox();
        unlimitedTextField.setTextColor(textColor);
    }
}
