package audiolibrary.service;

import audiolibrary.dao.PlaylistDAO;
import audiolibrary.model.Playlist;
import audiolibrary.model.Song;
import audiolibrary.model.User;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static audiolibrary.util.JsonUtil.*;

public class PlaylistService {

    private static final int PLAYLISTS_PER_PAGE = 5;
    private Map<User, List<Playlist>> userPlaylists = new HashMap<>();
    private PlaylistDAO playlistDAO = new PlaylistDAO();
    private PagingService<Playlist> pagingService = new PagingService<>();
    private SongService songService = new SongService();

    /**
     * Exports a specified playlist for a user in a specified format, currently only JSON is supported
     * @param user the user for which the playlist will be exported
     * @param playlistIdentifier the name or id of the playlist to be exported
     * @param format the format in which the playlist will be exported (Json)
     * @param <T> the type of the playlist identifier which can be Name/ID
     * @throws Exception if the playlist is not found, or the format is not supported
     */
    public <T> void exportPlaylist(User user, T playlistIdentifier, String format) throws Exception {
        /// will only work with JSON format
        loadPlaylists(user);
        List<Playlist> playlists = getUserPlaylists(user);

        Playlist playlist = null;

        if (playlistIdentifier instanceof Integer) {
            int id = (Integer) playlistIdentifier;
            for (Playlist p : playlists) {
                if (p.getId() == id) {
                    playlist = p;
                    break;
                }
            }
        } else if (playlistIdentifier instanceof String) {
            String name = (String) playlistIdentifier;
            for (Playlist p : playlists) {
                if (p.getName().equals(name)) {
                    playlist = p;
                    break;
                }
            }
        } else {
            throw new Exception("Invalid playlist identifier");
        }

        if (playlist == null) {
            throw new Exception("Playlist " + playlistIdentifier.toString() + " not found");
        }

        if(!"json".equalsIgnoreCase(format)) {
            throw new Exception("Unsupported format: " + format);
        }

        String filename = "export_" + user.getUsername() + "_" + playlist.getName() + "_" + getCurrentDate() + ".json";
        exportPlaylistToJson(playlist, filename);

        System.out.println("Playlist " + playlist.getName() + " exported successfully to " + filename);

    }

    private void exportPlaylistToJson(Playlist playlist, String filename) throws Exception {
        List<Song> songs = songService.getSongsByIds(playlist.getSongIds());
        ExportPLaylist exportPLaylist = new ExportPLaylist(playlist.getName(), songs);
        String playlistJson = new Gson().toJson(exportPLaylist);
        try(FileWriter writer = new FileWriter(filename)) {
            writer.write(playlistJson);
        } catch (Exception e) {
            throw new Exception("Failed to export playlist to JSON file");
        }
    }

    static class ExportPLaylist{
        private String playlistName;
        private List<Song> songs;

        public ExportPLaylist(String playlistName, List<Song> songs) {
            this.playlistName = playlistName;
            this.songs = songs;
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(new Date());
    }

    /**
     * Loads the playlists for a user if they are not already loaded from the JSON file
     * @param user the user for which the playlists will be loaded
     * @return true if the playlists were loaded successfully, false otherwise
     */
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

    /**
     * Creates a new playlist for a user, with the specified name, and saves it to the JSON file
     * @param user the user for which the playlist will be created
     * @param playlistName the name of the playlist to be created
     * @throws Exception if the playlist already exists, or if the playlists could not be loaded
     */
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


    /**
     * Adds one or multiple songs to a playlist for a user and saves the updated playlist to the JSON file
     * @param user the user for which the songs will be added to the playlist
     * @param playlistName the name of the playlist
     * @param songIds the ids of the songs that will be added
     * @param songLibrary the list of all songs in the library so we check if the songs exist
     * @throws Exception if the playlist does not exist, or if the songs do not exist
     */
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

        /// make the songIds unique with a set so I can't add [1,1,1,2,3,3,3,4] to a playlist
        Set<Integer> uniqueSongIds = new HashSet<>(songIds);
        List<Integer> uniqueSongIdsList = new ArrayList<>(uniqueSongIds);
        songIds = uniqueSongIdsList;

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

    /**
     * Lists all the playlists of a user, at a specified page, using paging
     * @param user the user for which the playlists will be listed
     * @param page the page number
     * @throws Exception if the playlists could not be loaded
     */
    public void listPlaylists(User user, int page) throws Exception {
        loadPlaylists(user);
        List<Playlist> playlists = getUserPlaylists(user);
        if (playlists == null) {
            System.out.println("You currently have no playlists.");
            return;
        }
        pagingService.listItems(playlists, page);
    }

    /**
     * Gets the playlist of a user. (method could be private)
     * @param user the user for which the playlist will be retrieved
     * @return the list of playlists of the user
     */
    public List<Playlist> getUserPlaylists(User user) {
        return userPlaylists.get(user);
    }
}