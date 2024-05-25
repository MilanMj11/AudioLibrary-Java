package audiolibrary.util;

import audiolibrary.model.Song;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    /**
     * Reads songs from a CSV file and returns a list of songs.
     * @param fileName the name of the file to read from
     * @return the list of songs read from the file
     */
    public static List<Song> readSongsFromCSV(String fileName) {
        List<Song> songs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int id = Integer.parseInt(values[0]);
                String name = values[1];
                String author = values[2];
                int releaseYear = Integer.parseInt(values[3]);
                songs.add(new Song(id, name, author, releaseYear));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return songs;
    }

    /**
     * Writes a list of songs to a CSV file.
     * @param fileName the name of the file to write to
     * @param songs the list of songs to write to the file
     */
    public static void writeSongsToCSV(String fileName, List<Song> songs) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Song song : songs) {
                bw.write(song.getId() + "," + song.getName() + "," + song.getAuthor() + "," + song.getReleaseYear());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}