package audiolibrary.model;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private int id;
    private String name;
    private String userId;
    private List<Integer> songIds;

    public Playlist(int id, String name, String userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songIds = new ArrayList<>();
    }

}