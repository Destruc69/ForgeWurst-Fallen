package net.wurstclient.forge.commands;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.wurstclient.forge.Command;
import net.wurstclient.forge.utils.ChatUtils;

import java.io.IOException;
import java.util.Objects;

public class DupeCmd extends Command {

    public DupeCmd() {
        super("dupe", "Duplicates items using a book & quill.", ".dupe",
                "How to use:", "1. Put book & quill in main hand.",
                "2. Place items to dupe in your inventory.",
                "3. Disconnect and reconnect.", "4. Place items in a chest.",
                "5. Run this command.");
    }

    @Override
    public void call(String[] args) throws CmdException, IOException {
        if (args.length > 0) {
            ChatUtils.error("Invalid syntax.");
            return;
        }

        EntityPlayer player = mc.player;

        if (player.getHeldItemMainhand().getItem() != Items.WRITABLE_BOOK) {
            ChatUtils.error("You must hold a book & quill in your main hand.");
            return;
        }

        NBTTagList listTag = new NBTTagList();

        StringBuilder builder1 = new StringBuilder();
        for (int i = 0; i < 21845; i++)
            builder1.append((char) 2077);

        listTag.appendTag(new NBTTagString(builder1.toString()));

        StringBuilder builder2 = new StringBuilder();
        for (int i = 0; i < 32; i++)
            builder2.append("Wurst!!!");

        String string2 = builder2.toString();
        for (int i = 1; i < 40; i++)
            listTag.appendTag(new NBTTagString(string2));

        ItemStack bookStack = new ItemStack(Items.WRITABLE_BOOK, 1);
        bookStack.setTagInfo("title", new NBTTagString("If you can see this, it didn't work"));
        bookStack.setTagInfo("pages", listTag);

        NBTTagCompound compound = bookStack.writeToNBT(new NBTTagCompound());
        ByteBuf buffer = Unpooled.buffer();
        new PacketBuffer(buffer).writeCompoundTag(compound);

        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketCustomPayload("MC|BSign", new PacketBuffer(buffer)));
    }
}
