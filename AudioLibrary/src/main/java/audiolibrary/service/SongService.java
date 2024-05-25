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

    public boolean createSong(String name, String author, int releaseYear) throws SongAlreadyExistsException {
        Song song = new Song(0, name, author, releaseYear);  // ID will be set in DAO
        if (!songDAO.createSong(song)) {
            throw new SongAlreadyExistsException();
        }
        return true;
    }

    public List<Song> getAllSongs() {
        return songDAO.getAllSongs();
    }

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

    public List<Song> getSongsByIds(List<Integer> ids) {
        return songDAO.getAllSongs().stream()
                .filter(song -> ids.contains(song.getId()))
                .collect(Collectors.toList());
    }

    public void listSongs(List<Song> songs, int page) throws Exception {
        pagingService.listItems(songs, page);
    }

}