package audiolibrary.service;

import audiolibrary.dao.PlaylistDAO;
import audiolibrary.model.Playlist;
import audiolibrary.model.Song;
import audiolibrary.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static audiolibrary.util.JsonUtil.*;

public class PlaylistService {

    private static final int SONGS_PER_PAGE = 5;
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


    public void createPlaylist(User user, String playlistName) throws Exception {
        initializeUserPlaylistsFile(user);

        boolean loadedSomething = loadPlaylists(user);

        if (!loadedSomething) {
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

    public void listPlaylists(User user, int page) throws Exception {
        loadPlaylists(user);
        List<Playlist> playlists = getUserPlaylists(user);
        if (playlists == null) {
            System.out.println("You currently have no playlists.");
            return;
        }

        int totalPlaylists = playlists.size();
        int totalPages = (int) Math.ceil((double) totalPlaylists / SONGS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            throw new Exception("Invalid page number");
        }

        int start = (page - 1) * SONGS_PER_PAGE;
        int end = Math.min(totalPlaylists, start + SONGS_PER_PAGE);

        System.out.println("Page " + page + " of " + totalPages + " (max " + SONGS_PER_PAGE + " items per page):");

        for (int i = start; i < end; i++) {
            Playlist playlist = playlists.get(i);
            System.out.println((i + 1) + ". " + playlist.getName());
        }

        if(totalPages > 1){
            System.out.println("To return to a desired page run the query as follows:");
            System.out.println("`list playlists <page_number>`");
        }

    }

    public List<Playlist> getUserPlaylists(User user) {
        return userPlaylists.get(user);
    }
}