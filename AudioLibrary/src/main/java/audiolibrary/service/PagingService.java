package audiolibrary.service;

import audiolibrary.model.Audit;
import audiolibrary.model.Playlist;
import audiolibrary.model.Song;

import java.awt.desktop.SystemEventListener;
import java.util.List;

public class PagingService<T> {
    private static final int ITEMS_PER_PAGE = 5;

    /**
     * Lists the items from a list at a given page
     * @param items the list of items that we want to display
     * @param page the page number
     * @throws Exception if the page number is invalid or if the list is empty
     */
    public void listItems(List<T> items, int page) throws Exception {
        if (items.isEmpty()) {
            if (items instanceof Song) {
                throw new Exception("No songs found");
            }
            if (items instanceof Playlist) {
                throw new Exception("No playlists found");
            }
            if (items instanceof Audit) {
                throw new Exception("No audit entries found");
            }
            throw new Exception("No items found");
        }

        int totalItems = items.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        if (page < 1 || page > totalPages) {
            throw new Exception("Invalid page number");
        }

        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(totalItems, start + ITEMS_PER_PAGE);

        System.out.println("Page " + page + " of " + totalPages + " (max " + ITEMS_PER_PAGE + " items per page):");

        for (int i = start; i < end; i++) {
            T item = items.get(i);
            System.out.println(itemToString(item));
        }

        if (totalPages > 1) {
            System.out.println();
            System.out.println("To return to a desired page run the query as follows:");
            System.out.println(itemCommandMessage(items.get(0)));
        }
    }

    private String itemToString(T item) {
        if (item instanceof Song) {
            Song song = (Song) item;
            return song.getName() + " - " + song.getAuthor() + " (" + song.getReleaseYear() + ")" + " [ID: " + song.getId() + "]";
        } else if (item instanceof Playlist) {
            Playlist playlist = (Playlist) item;
            return playlist.getName();
        } else if (item instanceof Audit) {
            Audit audit = (Audit) item;
            return audit.getUsername() + " | " + audit.getAction() + " | " + audit.isSuccess();
        } else {
            return item.toString();
        }
    }

    private String itemCommandMessage(T item) {
        if (item instanceof Playlist) {
            return "`list playlists <page_number>`";
        } else if (item instanceof Song) {
            return "`search <criteria> <Name> <page_number>`";
        } else if (item instanceof Audit) {
            return "`audit <username> <page_number>'";
        } else {
            return "`<command> <page_number>`";
        }
    }

}
