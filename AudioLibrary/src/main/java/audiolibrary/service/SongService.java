package audiolibrary.service;

import audiolibrary.dao.SongDAO;
import audiolibrary.exceptions.SongAlreadyExistsException;
import audiolibrary.model.Song;

import java.util.List;
import java.util.stream.Collectors;

public class SongService {
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

    /*
    public List<Song> searchSongsByName(String name) {
        return songDAO.getAllSongs().stream()
                .filter(song -> song.getName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Song> searchSongsByAuthor(String author) {
        return songDAO.getAllSongs().stream()
                .filter(song -> song.getAuthor().toLowerCase().startsWith(author.toLowerCase()))
                .collect(Collectors.toList());
    }
    */

}