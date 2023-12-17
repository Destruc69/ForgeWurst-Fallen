package net.wurstclient.forge.commands;

import net.wurstclient.forge.Command;
import net.wurstclient.forge.ForgeWurst;
import net.wurstclient.forge.utils.ChatUtils;
import net.wurstclient.forge.waypoints.Waypoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointsCmd extends Command {

    public WaypointsCmd() {
        super("waypoints", "Manages your waypoints.",
                ".waypoints add <x> <z>",
                ".waypoints remove <index>",
                ".waypoints remove-all",
                ".waypoints show-all");
    }

    public void call(String[] args) throws CmdException {
        if (args.length < 1) {
            throw new CmdSyntaxError("Missing subcommand.");
        }

        switch (args[0].toLowerCase()) {
            case "show-all":
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

            default:
                throw new CmdSyntaxError("Unknown subcommand: " + args[0]);
        }
    }

    private void add(String[] args) throws CmdException {
        if (args.length < 3) {
            throw new CmdSyntaxError("Missing waypoint coordinates.");
        }

        int x = Integer.parseInt(args[1]);
        int z = Integer.parseInt(args[2]);

        Waypoint waypoint = new Waypoint(obfuscate(x), obfuscate(z));
        ForgeWurst.getForgeWurst().getWaypoints().add(waypoint);
        ChatUtils.message("Waypoint added: " + decode(obfuscate(x)) + " " + decode(obfuscate(z)));
    }

    private void showAll(String[] args) throws CmdException {
        List<Waypoint> waypoints = getVectors();

        if (waypoints.isEmpty()) {
            ChatUtils.message("No waypoints available.");
            return;
        }

        int currentPage = 1;
        int waypointsPerPage = 10;
        int totalPages = (int) Math.ceil((double) waypoints.size() / waypointsPerPage);

        ChatUtils.message("Waypoints: Page " + currentPage + " of " + totalPages);
        for (int i = (currentPage - 1) * waypointsPerPage; i < Math.min(currentPage * waypointsPerPage, waypoints.size()); i++) {
            ChatUtils.message(i + 1 + ". " + decode(waypoints.get(i).getX()) + " " + decode(waypoints.get(i).getZ()));
        }
    }

    private void remove(String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError("Missing waypoint index.");
        }

        int index = Integer.parseInt(args[1]);

        if (index < 0 || index >= ForgeWurst.getForgeWurst().getWaypoints().getVectors().size()) {
            throw new CmdSyntaxError("Invalid waypoint index.");
        }

        Waypoint removedWaypoint = ForgeWurst.getForgeWurst().getWaypoints().get(index);
        ChatUtils.message("Waypoint removed: " + decode(removedWaypoint.getX()) + " " + decode(removedWaypoint.getZ()));
    }

    private void removeAll(String[] args) throws CmdException {
        if (ForgeWurst.getForgeWurst().getWaypoints().getVectors().isEmpty()) {
            ChatUtils.message("No waypoints to remove.");
            return;
        }

        ForgeWurst.getForgeWurst().getWaypoints().removeAll();
        ChatUtils.message("All waypoints removed.");
    }

    // Method to obfuscate the content of the notepad
    private Integer obfuscate(Integer note) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder obfuscatedNote = new StringBuilder();

        for (char character : String.valueOf(note).toCharArray()) {
            obfuscatedNote.append(substitutionMap.getOrDefault(character, character));
        }

        return Integer.parseInt(obfuscatedNote.toString());
    }

    public static Integer decode(int integers) {
        Map<Character, Character> substitutionMap = generateSubstitutionMap();
        StringBuilder decodedNote = new StringBuilder();

        for (char character : String.valueOf(integers).toCharArray()) {
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

        return Integer.parseInt(decodedNote.toString());
    }

    private static Map<Character, Character> generateSubstitutionMap() {
        Map<Character, Character> substitutionMap = new HashMap<>();

        // Define pairs of characters
        char[] originalChars = "1234567890".toCharArray();
        char[] substituteChars = "5418703629".toCharArray();

        // Populate the substitution map
        for (int i = 0; i < originalChars.length; i++) {
            substitutionMap.put(originalChars[i], substituteChars[i]);
        }

        return substitutionMap;
    }

    public List<Waypoint> getVectors() {
        return new ArrayList<>(ForgeWurst.getForgeWurst().getWaypoints().getVectors());
    }
}