package audiolibrary.dao;

import audiolibrary.model.Playlist;
import audiolibrary.util.JsonUtil;

import java.util.List;

public class PlaylistDAO {

    /**
     * Save playlists to json file
     * @param username The username of the user that owns the playlists
     * @param playlists The list of playlists that the user owns
     */
    public void savePlaylists(String username, List<Playlist> playlists) {
        JsonUtil.savePlaylistsToJson(username, playlists);
    }

    /**
     * Load playlists from json file
     * @param username The username of the user whose playlists are to be loaded
     * @return The list of playlists that the user owns
     */
    public List<Playlist> loadPlaylists(String username) {
        return JsonUtil.loadPlaylistsFromJson(username);
    }

}