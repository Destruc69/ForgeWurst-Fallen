package net.wurstclient.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.wurstclient.forge.utils.JsonUtils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

public class NotePad {
    private final Path path;
    private final TreeSet<String> notes = new TreeSet<>();

    public NotePad(Path path) {
        this.path = path;
    }

    public void init() {
        try {
            try (JsonReader reader = new JsonReader(new FileReader(path.toFile()))) {
                notes.clear();
                for (JsonElement s : JsonUtils.jsonParser.parse(reader).getAsJsonArray()) {
                    notes.add(s.getAsString());
                }
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
        }

        save();
    }

    public void loadDefaults() {
        notes.clear();
        // You can add default friends here if needed
        save();
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            JsonUtils.prettyGson.toJson(createJson(), writer);
        } catch (IOException e) {
            System.out.println("Failed to save " + path.getFileName());
            e.printStackTrace();
        }
    }

    private JsonArray createJson() {
        JsonArray json = new JsonArray();
        notes.forEach(json::add);
        return json;
    }

    public int size() {
        return notes.size();
    }

    public String get(int index) {
        // Converting TreeSet to an ArrayList for compatibility with the original structure
        return new ArrayList<>(notes).get(index);
    }

    public boolean contains(String str) {
        return notes.contains(str);
    }

    public void add(String note) {
        notes.add(note);
        save();
    }

    public void remove(String note) {
        notes.remove(note);
        save();
    }

    public void removeAll() {
        notes.clear();
        save();
    }

    public TreeSet<String> getNotes() {
        return notes;
    }
}
