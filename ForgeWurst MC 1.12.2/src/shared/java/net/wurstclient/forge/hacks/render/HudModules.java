package net.wurstclient.forge.hacks.render;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.hacks.ClickGuiHack;
import net.wurstclient.forge.other.customs.UnlimitedTextField;
import net.wurstclient.forge.settings.CheckboxSetting;
import net.wurstclient.forge.settings.SliderSetting;
import net.wurstclient.forge.utils.GUIUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class HudModules extends Hack {

    private int cx;
    private int cy;

    public final CheckboxSetting coords =
            new CheckboxSetting("Coords", "Shows your current position",
                    false);

    private final CheckboxSetting hide =
            new CheckboxSetting("Hide", "Wont show this module in enabled hacks",
                    false);

    public HudModules() {
        super("HudModules", "Use arrow keys and select move hud elements");
        setCategory(Category.RENDER);
        addSetting(coords);
        addSetting(hide);
    }

    private final SliderSetting yCoord =
            new SliderSetting("X-Coord", "Use arrow keys, this setting is used to save positions.", 40, -9999, 9999, 1, SliderSetting.ValueDisplay.DECIMAL);

    private final SliderSetting xCoord =
            new SliderSetting("Y-Coord", "Use arrow keys, this setting is used to save positions.", 40, -9999, 9999, 1, SliderSetting.ValueDisplay.DECIMAL);

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        cx = xCoord.getValueI();
        cy = yCoord.getValueI();
    }

    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public String getRenderName() {
        if (hide.isChecked()) {
            return "";
        } else {
            return "HudModules";
        }
    }

    @SubscribeEvent
    public void onRenderGUI(RenderGameOverlayEvent.Post event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            cy = (int) (cy + 0.3);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            cy = (int) (cy - 0.3);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            cx = (int) (cx + 0.3);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            cx = (int) (cx - 0.3);
        }

        xCoord.setValue(cx);
        yCoord.setValue(cy);

        if (coords.isChecked()) {
            GUIUtils.renderTextBoxForLabel(Math.round(mc.player.lastTickPosX) + " " + Math.round(mc.player.lastTickPosY) + " " + Math.round(mc.player.lastTickPosZ), cx, cy, 150, 25, Color.WHITE.getRGB());
        }
    }
}
