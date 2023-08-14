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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.wurstclient.fmlevents.WUpdateEvent;
import net.wurstclient.forge.Category;
import net.wurstclient.forge.Hack;
import net.wurstclient.forge.utils.KeyBindingUtils;

public class AutoEat extends Hack {
    private int oldSlot;
    private int bestSlot;

    public AutoEat() {
        super("AutoEat", "Eat food automatically");
        setCategory(Category.COMBAT);
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
                final ItemStack stack = mc.player.inventory.getStackInSlot(i);
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
                this.oldSlot = mc.player.inventory.currentItem;
            }
        }
        else {
            if (!this.canEat()) {
                this.stop();
                return;
            }
            if (!this.isFood(mc.player.inventory.getStackInSlot(this.bestSlot))) {
                this.stop();
                return;
            }
            mc.player.inventory.currentItem = this.bestSlot;
            KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, true);
        }
    }

    private boolean canEat() {
        if (!mc.player.canEat(false)) {
            return false;
        }
        if (Minecraft.getMinecraft().objectMouseOver != null) {
            final Entity entity = Minecraft.getMinecraft().objectMouseOver.entityHit;
            if (entity instanceof EntityVillager || entity instanceof EntityTameable) {
                return false;
            }
            final BlockPos pos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
            if (pos != null) {
                final Block block = mc.world.getBlockState(pos).getBlock();
                if (block instanceof BlockContainer || block instanceof BlockWorkbench) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isFood(final ItemStack stack) {
        return stack.getItem() instanceof ItemFood;
    }

    private void stop() {
        KeyBindingUtils.setPressed(mc.gameSettings.keyBindUseItem, false);
        mc.player.inventory.currentItem = this.oldSlot;
        this.oldSlot = -1;
    }
}
