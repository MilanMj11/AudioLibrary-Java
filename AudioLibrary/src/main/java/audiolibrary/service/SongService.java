package audiolibrary.service;

import audiolibrary.dao.SongDAO;
import audiolibrary.exceptions.SongAlreadyExistsException;
import audiolibrary.model.Song;

import java.util.List;
import java.util.stream.Collectors;

public class SongService {

    private static final int SONGS_PER_PAGE = 5;
    private SongDAO songDAO;

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

    public void listSongs(List<Song> songs, int page) throws Exception {
        if (songs.isEmpty()) {
            throw new Exception("No songs found");
        }

        int totalSongs = songs.size();
        int totalPages = (int) Math.ceil((double) totalSongs / SONGS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            throw new Exception("Invalid page number");
        }

        int start = (page - 1) * SONGS_PER_PAGE;
        int end = Math.min(totalSongs, start + SONGS_PER_PAGE);

        System.out.println("Page " + page + " of " + totalPages + " (max " + SONGS_PER_PAGE + " items per page):");

        for (int i = start; i < end; i++) {
            Song song = songs.get(i);
            System.out.println((i + 1) + ". " + song.getName() + " - " + song.getAuthor() + " (" + song.getReleaseYear() + ")" + " [ID: " + song.getId() + "]");
        }

        if (totalPages > 1) {
            System.out.println("To return to a desired page run the query as follows:");
            System.out.println("`search <criteria> <Name> <page_number>`");
        }

    }

}