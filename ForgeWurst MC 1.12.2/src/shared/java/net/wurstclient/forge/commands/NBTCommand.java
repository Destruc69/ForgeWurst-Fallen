package net.wurstclient.forge.commands;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

public class NBTCommand extends Command {

    public NBTCommand() {
        super("nbt", "Manipulate NBT tags on the player's held item.", "Syntax: .nbt get|remove|add|update <type> <tag> [value]");
    }

    @Override
    public void call(String[] args) throws CmdSyntaxError {
        ChatUtils.message("Not yet completed.");
       //String mode = String.valueOf(args[0]);
       //String type = String.valueOf(args[1]);
       //String tag = String.valueOf(args[2]);
       //String value = String.valueOf(args[3]);

       //ItemStack itemStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);

       //setTagWithType(itemStack.getTagCompound(), itemStack, type, tag, value, mode);
    }

    private void setTagWithType(NBTTagCompound nbtTagCompound, ItemStack itemStack, String type, String tag, String value, String mode) {
        if (mode.equalsIgnoreCase("add")) {
            if (type.equalsIgnoreCase("string")) {
                nbtTagCompound.setString(tag, value);
            } else if (type.equalsIgnoreCase("double")) {
                nbtTagCompound.setDouble(tag, Double.parseDouble(value));
            } else if (type.equalsIgnoreCase("integer")) {
                nbtTagCompound.setInteger(tag, Integer.parseInt(value));
            } else if (type.equalsIgnoreCase("boolean")) {
                nbtTagCompound.setBoolean(tag, Boolean.parseBoolean(value));
            } else {
                ChatUtils.error("Invalid type, The valid types are: string, double, integer, boolean");
            }
        } else if (mode.equalsIgnoreCase("remove")) {

        } else if (mode.equalsIgnoreCase("get")) {

        }
    }
}