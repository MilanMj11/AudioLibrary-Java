package audiolibrary.service;

import audiolibrary.dao.PlaylistDAO;
import audiolibrary.model.Playlist;
import audiolibrary.model.Song;
import audiolibrary.model.User;
import audiolibrary.util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static audiolibrary.util.JsonUtil.getNextPlaylistIdByUserFromJson;
import static audiolibrary.util.JsonUtil.savePlaylistsToJson;

public class PlaylistService {

    private Map<User, List<Playlist>> userPlaylists = new HashMap<>();
    private PlaylistDAO playlistDAO = new PlaylistDAO();

    public boolean loadPlaylists(User user) {
        /// if the user's playlists are already loaded, return
        if (userPlaylists.containsKey(user)) {
            return true;
        }
        List<Playlist> playlists = playlistDAO.loadPlaylists(user.getUsername());
        if (playlists != null) {
            userPlaylists.put(user, playlists);
            return true;
        }
        return false;
    }

    private void initializeUserPlaylistsFile(User user) throws Exception {
        String filename = "playlists_" + user.getUsername() + ".json";
        File file = new File(filename);
        if (!file.exists()) {
            List<Playlist> emptyPlaylists = new ArrayList<>();
            JsonUtil.savePlaylistsToJson(user.getUsername(), emptyPlaylists);
        }
    }

    public void createPlaylist(User user, String playlistName) throws Exception {
        initializeUserPlaylistsFile(user);

        boolean loadedSomething = loadPlaylists(user);

        if(!loadedSomething) {
            throw new Exception("Failed to load playlists for user " + user.getUsername());
        }

        List<Playlist> playlists = getUserPlaylists(user);
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(playlistName)) {
                throw new Exception("You already have a playlist named " + playlistName);
            }
        }

        int nextId = getNextPlaylistIdByUserFromJson(user.getUsername());
        Playlist playlist = new Playlist(nextId, playlistName, user.getUsername());
        playlists.add(playlist);

        playlistDAO.savePlaylists(user.getUsername(), playlists);
        System.out.println("Playlist \"" + playlistName + "\" was created successfully!");
    }

    public void addSongsToPlaylist(User user, String playlistName, List<Integer> songIds, List<Song> songLibrary) throws Exception {
        loadPlaylists(user);
        List<Playlist> playlists = getUserPlaylists(user);
        if (playlists == null) {
            throw new Exception("You currently have no playlists.");
        }
        Playlist playlist = null;
        for (Playlist pl : playlists) {
            if (pl.getName().equals(playlistName)) {
                playlist = pl;
                break;
            }
        }
        if (playlist == null) {
            throw new Exception("The desired playlist does not exist.");
        }
        for (Integer songId : songIds) {
            boolean songExists = false;

            for (Song song : songLibrary) {
                if (song.getId() == songId) {
                    songExists = true;
                    break;
                }
            }

            if (songExists == false) {
                throw new Exception("Song with identifier " + songId + " does not exist.");
            }
            if (playlist.getSongIds().contains(songId)) {
                throw new Exception("The song with ID " + songId + " is already part of \"" + playlistName + "\"");
            }
        }
        playlist.getSongIds().addAll(songIds);
        playlistDAO.savePlaylists(user.getUsername(), playlists);
        System.out.println("Added songs to \"" + playlistName + "\" successfully!");
    }

    public List<Playlist> getUserPlaylists(User user) {
        return userPlaylists.get(user);
    }
}