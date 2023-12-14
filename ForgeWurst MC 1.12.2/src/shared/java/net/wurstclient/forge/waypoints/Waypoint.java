package net.wurstclient.forge.waypoints;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Waypoint {

    private int x;
    private int y;
    private int z;

    public Waypoint() {
    }

    public Waypoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static Waypoint fromJsonElement(JsonElement element) {
        Waypoint waypoint = new Waypoint();

        if (element.isJsonArray()) {
            JsonArray jsonArray = element.getAsJsonArray();

            if (jsonArray.size() == 3) {
                waypoint.x = jsonArray.get(0).getAsInt();
                waypoint.y = jsonArray.get(1).getAsInt();
                waypoint.z = jsonArray.get(2).getAsInt();
            } else {
                throw new IllegalArgumentException("JsonArray must have exactly three elements for vector.");
            }
        } else {
            throw new IllegalArgumentException("JsonElement must be a JsonArray for vector deserialization.");
        }

        return waypoint;
    }

    public JsonElement toJsonElement() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(x);
        jsonArray.add(y);
        jsonArray.add(z);
        return jsonArray;
    }
}
