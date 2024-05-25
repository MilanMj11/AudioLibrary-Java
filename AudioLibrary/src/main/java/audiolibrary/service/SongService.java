package audiolibrary.service;

import audiolibrary.dao.SongDAO;
import audiolibrary.exceptions.SongAlreadyExistsException;
import audiolibrary.model.Song;

import java.util.List;
import java.util.stream.Collectors;

public class SongService {

    private static final int SONGS_PER_PAGE = 5;
    private SongDAO songDAO;
    private PagingService<Song> pagingService = new PagingService<>();

    public SongService() {
        this.songDAO = new SongDAO();
    }

    /**
     * Create a new song and add it to the csv file
     * @param name the name of the song
     * @param author the author of the song
     * @param releaseYear the realase year of the song
     * @return true if the song was created successfully
     * @throws SongAlreadyExistsException if the song already exists
     */
    public boolean createSong(String name, String author, int releaseYear) throws SongAlreadyExistsException {
        Song song = new Song(0, name, author, releaseYear);  // ID will be set in DAO
        if (!songDAO.createSong(song)) {
            throw new SongAlreadyExistsException();
        }
        return true;
    }

    /**
     * Gets all songs from the csv file
     * @return a list of all songs
     */
    public List<Song> getAllSongs() {
        return songDAO.getAllSongs();
    }

    /**
     * Search for a song by a given criteria and a search term of the criteria
     * @param criteria can be "name" or "author"
     * @param searchTerm the term to search for which could be a name of an artist or a song
     * @return a list of songs that match the search criteria
     * @throws Exception if the criteria is invalid
     */
    public List<Song> searchSongs(String criteria, String searchTerm) throws Exception {
        criteria = criteria.toLowerCase();
        switch (criteria) {
            case "name":
                return songDAO.getAllSongs().stream()
                        .filter(song -> song.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList());
            case "author":
                return songDAO.getAllSongs().stream()
                        .filter(song -> song.getAuthor().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList());
            default:
                throw new Exception("Invalid search criteria");
        }
    }

    /**
     * Get a list of songs by their ids
     * @param ids a list of integers representing the ids of the songs
     * @return a list of songs that match the ids
     */
    public List<Song> getSongsByIds(List<Integer> ids) {
        return songDAO.getAllSongs().stream()
                .filter(song -> ids.contains(song.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Lists a list of songs at a given page number ( using paging )
     * @param songs the list of songs to be shown
     * @param page the page number
     * @throws Exception if the page number is invalid
     */
    public void listSongs(List<Song> songs, int page) throws Exception {
        pagingService.listItems(songs, page);
    }

}