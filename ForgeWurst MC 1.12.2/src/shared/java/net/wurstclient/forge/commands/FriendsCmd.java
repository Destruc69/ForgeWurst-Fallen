package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.FriendsList;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class FriendsCmd extends Command
{
    private static final int FRIENDS_PER_PAGE = 8;

    public FriendsCmd()
    {
        super("friends", "Manages your friends list.", ".friends add <name>",
                ".friends remove <name>", ".friends remove-all",
                ".friends list [<page>]");
    }

    @Override
    public void call(String[] args) throws CmdException
    {
        if(args.length < 1 || args.length > 2)
            throw new CmdSyntaxError();

        switch(args[0].toLowerCase())
        {
            case "add":
                add(args);
                break;

            case "remove":
                remove(args);
                break;

            case "remove-all":
                removeAll(args);
                break;

            case "list":
                list(args);
                break;

            default:
                throw new CmdSyntaxError();
        }
    }

    private void add(String[] args) throws CmdException
    {
        if(args.length != 2)
            throw new CmdSyntaxError();

        String name = args[1];
        if(ForgeWurst.getForgeWurst().getFriendsList().contains(name))
            throw new CmdError(
                    "\"" + name + "\" is already in your friends list.");

        ForgeWurst.getForgeWurst().getFriendsList().add(name);
        ChatUtils.message("Added friend \"" + name + "\".");
    }

    private void remove(String[] args) throws CmdException
    {
        if(args.length != 2)
            throw new CmdSyntaxError();

        String name = args[1];
        if(!ForgeWurst.getForgeWurst().getFriendsList().contains(name))
            throw new CmdError("\"" + name + "\" is not in your friends list.");

        ForgeWurst.getForgeWurst().getFriendsList().remove(name);
        ChatUtils.message("Removed friend \"" + name + "\".");
    }

    private void removeAll(String[] args) throws CmdException
    {
        if(args.length > 1)
            throw new CmdSyntaxError();

        ForgeWurst.getForgeWurst().getFriendsList().removeAll();
        ChatUtils.message("All friends removed. Oof.");
    }

    private void list(String[] args) throws CmdException {
        if (args.length > 2)
            throw new CmdSyntaxError();

        FriendsList friendsList = ForgeWurst.getForgeWurst().getFriendsList();
        TreeSet<String> friends = friendsList.getFriends();
        int page = parsePage(args);
        int pages = (int) Math.ceil(friends.size() / (double) FRIENDS_PER_PAGE);
        pages = Math.max(pages, 1);

        if (page > pages || page < 1)
            throw new CmdSyntaxError("Invalid page: " + page);

        ChatUtils.message("Current friends: " + friends.size());

        int start = (page - 1) * FRIENDS_PER_PAGE;
        int end = Math.min(page * FRIENDS_PER_PAGE, friends.size());

        List<String> friendsListAsList = new ArrayList<>(friends);

        ChatUtils.message("Friends list (page " + page + "/" + pages + ")");
        for (int i = start; i < end; i++)
            ChatUtils.message(friendsListAsList.get(i));
    }


    private int parsePage(String[] args) throws CmdSyntaxError
    {
        if(args.length < 2)
            return 1;

        if(!MathUtils.isInteger(args[1]))
            throw new CmdSyntaxError("Not a number: " + args[1]);

        return Integer.parseInt(args[1]);
    }
}