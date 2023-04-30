package net.wurstclient.forge.hacks.render;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.IngameHUD;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.CheckboxSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class HudModules extends Hack {

    public final CheckboxSetting coords =
            new CheckboxSetting("Coords", "Shows your current position",
                    false);
    public static double coordX;
    public static double coordY;

    public final CheckboxSetting speed =
            new CheckboxSetting("Speed", "Shows your current motion speed",
                    false);
    public static double speedX;
    public static double speedY;
    private final CheckboxSetting hide =
            new CheckboxSetting("Hide", "Wont show this module in enabled hacks",
                    false);

    public HudModules() {
        super("HudModules", "Use arrow keys and select move hud elements");
        setCategory(Category.RENDER);
        addSetting(coords);
        addSetting(speed);
        addSetting(hide);
    }

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
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
        if (coords.isChecked()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    coordY = coordY - 0.25;
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    coordY = coordY + 0.25;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    coordX = coordX + 0.25;
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    coordX = coordX - 0.25;
                }
            }
        }
        if (speed.isChecked()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    speedY = speedY - 0.25;
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    speedY = speedY + 0.25;
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    speedX = speedX + 0.25;
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                if (mc.player.ticksExisted % 2 == 0) {
                    speedX = speedX - 0.25;
                }
            }
        }
    }
}
