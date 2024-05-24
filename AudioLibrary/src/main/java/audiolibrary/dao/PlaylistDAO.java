package audiolibrary.dao;

import audiolibrary.model.Playlist;
import audiolibrary.util.JsonUtil;

import java.util.List;

public class PlaylistDAO {

    public void savePlaylists(String username, List<Playlist> playlists) {
        JsonUtil.savePlaylistsToJson(username, playlists);
    }

    public List<Playlist> loadPlaylists(String username) {
        return JsonUtil.loadPlaylistsFromJson(username);
    }

}