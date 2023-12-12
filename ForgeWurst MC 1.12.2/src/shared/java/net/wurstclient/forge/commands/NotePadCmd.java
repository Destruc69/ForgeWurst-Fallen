package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.utils.ChatUtils;

public class NotePadCmd extends Command {

    public NotePadCmd() {
        super("notepad", "Manages your notes.",
                ".notepad add <note>",
                ".notepad remove <index>",
                ".notepad remove-all",
                ".notepad show [<index>]",
                ".notepad showall");
    }

    @Override
    public void call(String[] args) throws CmdException {
        if (args.length < 1) {
            throw new CmdSyntaxError("Missing subcommand.");
        }

        switch (args[0].toLowerCase()) {
            case "showall":
                showAll(args);
                break;

            case "add":
                add(args);
                break;

            case "remove":
                remove(args);
                break;

            case "remove-all":
                removeAll(args);
                break;

            case "show":
                showNote(args);
                break;
            default:
                throw new CmdSyntaxError("Unknown subcommand: " + args[0]);
        }
    }

    private void add(String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError("Missing note text.");
        }

        String note = args[1];
        ForgeWurst.getForgeWurst().getNotePad().add(note);
        ChatUtils.message("Note added: " + note);
    }

    private void remove(String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError("Missing note index.");
        }

        try {
            int index = Integer.parseInt(args[1]);
            if (index < 1 || index > ForgeWurst.getForgeWurst().getNotePad().size()) {
                throw new CmdSyntaxError("Invalid note index: " + index);
            }

            String removedNote = ForgeWurst.getForgeWurst().getNotePad().get(index);
            ForgeWurst.getForgeWurst().getNotePad().remove(removedNote);
            ChatUtils.message("Note removed: " + removedNote);
        } catch (NumberFormatException e) {
            throw new CmdSyntaxError("Not a valid number: " + args[1]);
        }
    }

    private void removeAll(String[] args) throws CmdException {
        if (args.length != 1) {
            throw new CmdSyntaxError("Unexpected arguments.");
        }

        ForgeWurst.getForgeWurst().getNotePad().removeAll();
        ChatUtils.message("All notes removed.");
    }

    private void showAll(String[] args) throws CmdException {
        // Check if there are no notes
        if (ForgeWurst.getForgeWurst().getNotePad().size() <= 0) {
            ChatUtils.message("No notes available.");
            return;
        }

        int currentPage = 1;
        int notesPerPage = 10;
        int totalPages = (int) Math.ceil((double) ForgeWurst.getForgeWurst().getNotePad().size() / notesPerPage);

        ChatUtils.message("Notes: Page " + currentPage + " of " + totalPages);
        for (int i = (currentPage - 1) * notesPerPage; i < Math.min(currentPage * notesPerPage, ForgeWurst.getForgeWurst().getNotePad().size()); i++) {
            ChatUtils.message(i + 1 + ". " + ForgeWurst.getForgeWurst().getNotePad().get(i));
        }
    }

    private void showNote(String[] args) throws CmdException {
        int index;

        try {
            index = Integer.parseInt(args[1]);
            if (index < 1 || index > ForgeWurst.getForgeWurst().getNotePad().size()) {
                throw new CmdSyntaxError("Invalid note index: " + index);
            }

            String note = ForgeWurst.getForgeWurst().getNotePad().get(index - 1);
            ChatUtils.message("Note " + index + ": " + note);

        } catch (NumberFormatException e) {
            if (args.length != 2 || !args[1].matches("^[a-f0-9]+$")) {
                throw new CmdSyntaxError("Invalid note identifier: " + args[1]);
            }

            index = Integer.parseInt(ForgeWurst.getForgeWurst().getNotePad().get(Integer.parseInt(args[1])));
            if (index == -1) {
                throw new CmdSyntaxError("No note found with identifier: " + args[1]);
            }
        }
    }
}