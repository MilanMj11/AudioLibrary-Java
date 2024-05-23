package audiolibrary.dao;

import audiolibrary.model.Playlist;
import audiolibrary.util.JsonUtil;

import java.util.List;
import java.util.Map;

public class PlaylistDAO {

    public void savePlaylists(String username, List<Playlist> playlists) {
        JsonUtil.savePlaylistsToJson(username, playlists);
    }

    public List<Playlist> loadPlaylists(String username) {
        return JsonUtil.loadPlaylistsFromJson(username);
    }

    public void saveNextPlaylistIdByUser(Map<String, Integer> nextPlaylistIdByUser) {
        JsonUtil.saveNextPlaylistIdByUserToJson(nextPlaylistIdByUser);
    }

    public Map<String, Integer> loadNextPlaylistIdByUser() {
        return JsonUtil.loadNextPlaylistIdByUserFromJson();
    }
}