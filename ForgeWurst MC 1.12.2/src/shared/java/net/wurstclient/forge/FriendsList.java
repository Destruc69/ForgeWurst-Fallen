package net.wurstclient.forge;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.wurstclient.forge.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public final class FriendsList {
    private final Path path;
    private final ArrayList<Friend> friends = new ArrayList<>();

    public FriendsList(Path file)
    {
        path = file;
    }

    public void init()
    {
        JsonObject json;
        try(BufferedReader reader = Files.newBufferedReader(path))
        {
            json = JsonUtils.jsonParser.parse(reader).getAsJsonObject();

        }catch(NoSuchFileException e)
        {
            loadDefaults();
            return;

        }catch(Exception e)
        {
            System.out.println("Failed to load " + path.getFileName());
            e.printStackTrace();

            loadDefaults();
            return;
        }

        friends.clear();

        TreeMap<String, String> friendd = new TreeMap<>();
        for(Map.Entry<String, JsonElement> entry : json.entrySet())
        {
            String name = entry.getKey().toUpperCase();
            if(!entry.getValue().isJsonPrimitive()
                    || !entry.getValue().getAsJsonPrimitive().isString())
                continue;
            String tag = entry.getValue().getAsString();

            friendd.put(name, tag);
        }

        for(Map.Entry<String, String> entry : friendd.entrySet())
            friends.add(new Friend(entry.getKey(), entry.getValue()));

        save();
    }

    public void loadDefaults()
    {
        friends.clear();
        save();
    }

    private void save()
    {
        JsonObject json = new JsonObject();
        for(Friend friend : friends)
            json.addProperty(friend.name, friend.tag);

        try(BufferedWriter writer = Files.newBufferedWriter(path))
        {
            JsonUtils.prettyGson.toJson(json, writer);

        }catch(IOException e)
        {
            System.out.println("Failed to save " + path.getFileName());
            e.printStackTrace();
        }
    }

    public int size()
    {
        return friends.size();
    }

    public Friend get(int index)
    {
        return friends.get(index);
    }

    public void add(String name, String tag)
    {
        friends.add(new Friend(name, tag));
        save();
    }

    public void remove(int index)
    {
        friends.remove(index);
        save();
    }

    public void removeAll()
    {
        friends.clear();
        save();
    }

    public static class Friend
    {
        private final String name;
        private final String tag;

        public Friend(String name, String tag)
        {
            this.name = name;
            this.tag = tag;
        }

        public String getName()
        {
            return name;
        }

        public String getTag()
        {
            return tag;
        }
    }
}
