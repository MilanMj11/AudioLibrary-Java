package audiolibrary.util;

import audiolibrary.model.Playlist;
import audiolibrary.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String PLAYLISTS_DIR = "userPlaylists";

    /**
     * Initializes the playlists JSON file for a user. If the file does not exist, it creates an empty one.
     * @param user the user for which the playlists file will be created.
     */
    public static void initializeUserPlaylistsFile(User user) {
        String filename = PLAYLISTS_DIR + "/playlists_" + user.getUsername() + ".json";
        File file = new File(filename);
        if (!file.exists()) {
            List<Playlist> emptyPlaylists = new ArrayList<>();
            savePlaylistsToJson(user.getUsername(), emptyPlaylists);
        }
    }

    /**
     * Saves the playlists of a user to his JSON file.
     * @param username the username of the user.
     * @param playlists the playlists to be saved.
     */
    public static void savePlaylistsToJson(String username, List<Playlist> playlists) {
        String json = gson.toJson(playlists);
        try (FileWriter writer = new FileWriter(PLAYLISTS_DIR + "/playlists_" + username + ".json")) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Loads the playlists of a user from his JSON file.
     * @param username the username of the user.
     * @return
     */
    public static List<Playlist> loadPlaylistsFromJson(String username) {
        String jsonFilePath = PLAYLISTS_DIR + "/playlists_" + username + ".json";
        try (FileReader reader = new FileReader(jsonFilePath)) {
            Type playlistListType = new TypeToken<ArrayList<Playlist>>() {}.getType();
            return gson.fromJson(reader, playlistListType);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Gets the next playlist id for a user by accessing his playlists JSON file to find the maximum id.
     * @param username the username of the user.
     * @return the next playlist id.
     */
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
