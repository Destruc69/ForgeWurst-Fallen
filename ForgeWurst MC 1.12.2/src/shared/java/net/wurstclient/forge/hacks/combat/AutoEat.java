package net.wurstclient.forge.hacks.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.compatibility.WItem;
import net.wurstclient.forge.compatibility.WMinecraft;
import net.wurstclient.forge.settings.EnumSetting;
import net.wurstclient.forge.utils.KeyBindingUtils;

public class AutoEat extends Hack {
    private int oldSlot;
    private int bestSlot;

    private static final EnumSetting<Mode> mode =
            new EnumSetting<>("Mode", Mode.values(), Mode.LEGIT);

    public AutoEat() {
        super("AutoEat", "Eat food automatically");
        setCategory(Category.COMBAT);
        addSetting(mode);
    }

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);

        this.oldSlot = -1;
        this.bestSlot = -1;
    }

    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onUpdate(WUpdateEvent event) {
        if (this.oldSlot == -1) {
            if (!this.canEat()) {
                return;
            }
            float bestSaturation = 0.0f;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = WMinecraft.getPlayer().inventory.getStackInSlot(i);
                if (this.isFood(stack)) {
                    final ItemFood food = (ItemFood)stack.getItem();
                    final float saturation = food.getSaturationModifier(stack);
                    if (saturation > bestSaturation) {
                        bestSaturation = saturation;
                        this.bestSlot = i;
                    }
                }
            }
            if (this.bestSlot != -1) {
                this.oldSlot = WMinecraft.getPlayer().inventory.currentItem;
            }
        }
        else {
            if (!this.canEat()) {
                this.stop();
                return;
            }
            if (!this.isFood(WMinecraft.getPlayer().inventory.getStackInSlot(this.bestSlot))) {
                this.stop();
                return;
            }

            if (mode.getSelected() == Mode.LEGIT) {
                WMinecraft.getPlayer().inventory.currentItem = this.bestSlot;
                KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, true);
            } else {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(bestSlot));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
        }
    }

    private boolean canEat() {
        if (!WMinecraft.getPlayer().canEat(false)) {
            return false;
        }
        if (Minecraft.getMinecraft().objectMouseOver != null) {
            final Entity entity = Minecraft.getMinecraft().objectMouseOver.entityHit;
            if (entity instanceof EntityVillager || entity instanceof EntityTameable) {
                return false;
            }
            final BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
            if (pos != null) {
                final Block block = WMinecraft.getWorld().getBlockState(pos).getBlock();
                return !(block instanceof BlockContainer) && !(block instanceof BlockWorkbench);
            }
        }
        return true;
    }

    private boolean isFood(final ItemStack stack) {
        return !WItem.isNullOrEmpty(stack) && stack.getItem() instanceof ItemFood;
    }

    private void stop() {
        if (mode.getSelected() == Mode.LEGIT) {
            KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
            WMinecraft.getPlayer().inventory.currentItem = this.oldSlot;
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        }
        this.oldSlot = -1;
    }

    private enum Mode {
        PACKET("Packet"),
        LEGIT("Legit");

        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}