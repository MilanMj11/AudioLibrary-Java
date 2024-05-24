package audiolibrary.util;

import audiolibrary.model.Playlist;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void savePlaylistsToJson(String username, List<Playlist> playlists) {
        String json = gson.toJson(playlists);
        try (FileWriter writer = new FileWriter("playlists_" + username + ".json")) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Playlist> loadPlaylistsFromJson(String username) {
        try (FileReader reader = new FileReader("playlists_" + username + ".json")) {
            Type playlistListType = new TypeToken<List<Playlist>>(){}.getType();
            return gson.fromJson(reader, playlistListType);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Integer getNextPlaylistIdByUserFromJson(String username) {
        List<Playlist> playlists = loadPlaylistsFromJson(username);
        if(playlists == null || playlists.isEmpty()) {
            return 1;
        }
        int maxId = 1;
        for(Playlist playlist : playlists) {
            if (playlist.getId() > maxId) {
                maxId = playlist.getId();
            }
        }
        return maxId + 1;
    }

}
