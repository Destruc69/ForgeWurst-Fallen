package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.utils.ChatUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        if (args.length < 2) {
            throw new CmdSyntaxError("Missing note text.");
        }

        String note = obfuscate( String.join(" ", Arrays.copyOfRange(args, 1, args.length)) );
        ForgeWurst.getForgeWurst().getNotePad().add(note);
        ChatUtils.message("Note added: " + decode( note ));
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

            String removedNote = obfuscate( ForgeWurst.getForgeWurst().getNotePad().get(index - 1) );
            ForgeWurst.getForgeWurst().getNotePad().remove(removedNote);
            ChatUtils.message("Note removed: " + decode( removedNote ));
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
            ChatUtils.message(i + 1 + ". " + decode( ForgeWurst.getForgeWurst().getNotePad().get(i)) );
        }
    }

    private void showNote(String[] args) throws CmdException {
        int index;

        try {
            index = Integer.parseInt(args[1]);
            if (index < 1 || index > ForgeWurst.getForgeWurst().getNotePad().size()) {
                throw new CmdSyntaxError("Invalid note index: " + index);
            }

            String note = decode( ForgeWurst.getForgeWurst().getNotePad().get(index - 1) );
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

    // Method to obfuscate the content of the notepad
    private String obfuscate(String note) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder obfuscatedNote = new StringBuilder();

        for (char character : note.toCharArray()) {
            obfuscatedNote.append(substitutionMap.getOrDefault(character, character));
        }

        return obfuscatedNote.toString();
    }

    private String decode(String obfuscatedNote) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder decodedNote = new StringBuilder();

        for (char character : obfuscatedNote.toCharArray()) {
            if (character == ' ') {
                // Handle spaces
                decodedNote.append(' ');
            } else {
                // Handle other characters using the substitution map
                boolean found = false;
                for (Map.Entry<Character, Character> entry : substitutionMap.entrySet()) {
                    if (entry.getValue() == character) {
                        decodedNote.append(entry.getKey());
                        found = true;
                        break;
                    }
                }

                // If the character is not found in the substitution map, append it as is
                if (!found) {
                    decodedNote.append(character);
                }
            }
        }

        return decodedNote.toString();
    }

    private Map<Character, Character> generateSubstitutionMap() {
        Map<Character, Character> substitutionMap = new HashMap<>();

        // Define pairs of characters
        char[] originalChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        char[] substituteChars = "VD0oeBtPb6whKJRUnCqLNvzIGOAs8jZXkS4Q25H9racyxigmlufM3dYE7Wp1TF".toCharArray();

        // Populate the substitution map
        for (int i = 0; i < originalChars.length; i++) {
            substitutionMap.put(originalChars[i], substituteChars[i]);
        }

        return substitutionMap;
    }
}