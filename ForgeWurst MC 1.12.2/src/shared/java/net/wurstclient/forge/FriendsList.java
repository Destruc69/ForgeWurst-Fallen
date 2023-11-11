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

public final class FriendsList {
    private final Path path;
    private final TreeSet<String> friends = new TreeSet<>();

    public FriendsList(Path path) {
        this.path = path;
    }

    public void init() {
        try {
            try (JsonReader reader = new JsonReader(new FileReader(path.toFile()))) {
                friends.clear();
                for (JsonElement s : JsonUtils.jsonParser.parse(reader).getAsJsonArray()) {
                    friends.add(s.getAsString());
                }
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
        }

        save();
    }

    public void loadDefaults() {
        friends.clear();
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
        friends.forEach(json::add);
        return json;
    }

    public int size() {
        return friends.size();
    }

    public String get(int index) {
        // Converting TreeSet to an ArrayList for compatibility with the original structure
        return new ArrayList<>(friends).get(index);
    }

    public boolean contains(String name) {
        return friends.contains(name);
    }

    public void add(String name) {
        friends.add(name);
        save();
    }

    public void remove(String name) {
        friends.remove(name);
        save();
    }

    public void removeAll() {
        friends.clear();
        save();
    }

    public TreeSet<String> getFriends() {
        return friends;
    }
}