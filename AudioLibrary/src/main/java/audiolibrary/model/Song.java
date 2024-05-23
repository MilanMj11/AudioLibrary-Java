package audiolibrary.model;

public class Song {

    private int id;
    private String name;
    private String author;
    private int releaseYear;

    public Song(int id, String name, String author, int releaseYear) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.releaseYear = releaseYear;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReleaseYear() {
        return this.releaseYear;
    }
}