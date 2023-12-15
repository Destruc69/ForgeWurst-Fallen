package net.wurstclient.forge.waypoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Waypoint implements Comparable<Waypoint> {

    private int x;
    private int z;

    public Waypoint() {
    }

    public Waypoint(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public static Waypoint fromJsonElement(JsonElement element) {
        Waypoint waypoint = new Waypoint();

        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();

            if (jsonArray.size() == 2) {  // Corrected the condition
                waypoint.x = jsonArray.get(0).getAsInt();
                waypoint.z = jsonArray.get(1).getAsInt();
            } else {
                throw new IllegalArgumentException("JsonArray must have exactly two elements for vector.");  // Updated the error message
            }
        } else {
            throw new IllegalArgumentException("JsonElement must be a JsonArray for vector deserialization.");
        }

        return waypoint;
    }

    public JsonElement toJsonElement() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(x);
        jsonArray.add(z);
        return jsonArray;
    }

    @Override
    public int compareTo(Waypoint other) {
        // Implement your comparison logic here
        // For example, compare based on x and z values
        if (this.x != other.x) {
            return Integer.compare(this.x, other.x);
        }
        return Integer.compare(this.z, other.z);
    }
}
