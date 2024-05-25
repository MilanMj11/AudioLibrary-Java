package audiolibrary.dao;

import audiolibrary.exceptions.SongAlreadyExistsException;
import audiolibrary.model.Song;
import audiolibrary.util.CSVUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class SongDAO {
    private static final String CSV_FILE = "songs.csv";
    private AtomicInteger currentId = new AtomicInteger(0);

    public SongDAO() {
        List<Song> songs = getAllSongs();
        if (!songs.isEmpty()) {
            currentId.set(songs.stream().mapToInt(Song::getId).max().orElse(0));
        }
    }

    /**
     * Generate the new id for a song
     * @return the new id
     */
    private int generateId() {
        return currentId.incrementAndGet();
    }

    /**
     * Reads all the songs from the CSV file
     * @return a list of all the songs
     */
    public List<Song> getAllSongs() {
        return CSVUtil.readSongsFromCSV(CSV_FILE);
    }

    /**
     * Writes the newly created song to the CSV file
     * @param song the song to be written
     * @return true if the song was successfully written
     * @throws SongAlreadyExistsException if the song with the same name and author already exists
     */
    public boolean createSong(Song song) throws SongAlreadyExistsException {
        List<Song> songs = getAllSongs();
        if (songs.stream().anyMatch(s -> s.getName().equals(song.getName()) && s.getAuthor().equals(song.getAuthor()))) {
            throw new SongAlreadyExistsException();
        }
        song.setId(generateId());
        songs.add(song);
        CSVUtil.writeSongsToCSV(CSV_FILE, songs);
        return true;
    }
}