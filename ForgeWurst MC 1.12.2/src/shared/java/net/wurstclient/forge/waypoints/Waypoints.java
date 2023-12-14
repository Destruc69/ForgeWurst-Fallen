package net.wurstclient.forge.waypoints;

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

public class Waypoints {
    private final Path path;
    private final TreeSet<Waypoint> vectors = new TreeSet<>();

    public Waypoints(Path path) {
        this.path = path;
    }

    public void init() {
        try {
            try (JsonReader reader = new JsonReader(new FileReader(path.toFile()))) {
                vectors.clear();
                for (JsonElement element : JsonUtils.jsonParser.parse(reader).getAsJsonArray()) {
                    // Assuming Waypoint has appropriate methods for deserialization
                    Waypoint vector = Waypoint.fromJsonElement(element);
                    vectors.add(vector);
                }
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
        }

        save();
    }

    public void loadDefaults() {
        vectors.clear();
        // You can add default vectors here if needed
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
        vectors.forEach(vector -> json.add(vector.toJsonElement()));
        return json;
    }

    public int size() {
        return vectors.size();
    }

    public Waypoint get(int index) {
        // Converting TreeSet to an ArrayList for compatibility with the original structure
        return new ArrayList<>(vectors).get(index);
    }

    public boolean contains(Waypoint vector) {
        return vectors.contains(vector);
    }

    public void add(Waypoint vector) {
        vectors.add(vector);
        save();
    }

    public void remove(Waypoint vector) {
        vectors.remove(vector);
        save();
    }

    public void removeAll() {
        vectors.clear();
        save();
    }

    public TreeSet<Waypoint> getVectors() {
        return vectors;
    }
}

