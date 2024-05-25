package audiolibrary.model;

import audiolibrary.service.PagingService;

import java.util.List;

public class Helper {
    private static Helper instance;
    private List<String> commands = List.of(
            "register <username> <password>",
            "login <username> <password>",
            "logout",
            "promote <username>",
            "create song <name> <author> <releaseYear>",
            "create playlist <name>",
            "add <playlistName> [<songId>, ...]",
            "list playlists",
            "search name <name>",
            "search author <author>",
            "export playlist <playlistName> json",
            "audit",
            "help",
            "exit"
    );

    private Helper() {
    }

    public static synchronized Helper getInstance() {
        if (instance == null) {
            instance = new Helper();
        }
        return instance;
    }

    private List<String> getAvailableCommands(User user) {
        if (user == null) {
            return List.of("register",
                    "login",
                    "help",
                    "exit");
        }
        if (user.isAdmin() == false) {
            return List.of(
                    "logout",
                    "create playlist <name>",
                    "add <playlistName> [<songId>, ...]",
                    "list playlists",
                    "search name <name>",
                    "search author <author>",
                    "export playlist <playlistName> json",
                    "help",
                    "exit"
            );
        }
        /// Without login and register ofc.
        return this.commands.subList(2, this.commands.size());
    }

    public void showCommands(User user, int page) {
        PagingService<String> pagingService = new PagingService<>();
        try {
            List<String> availableCommands = getAvailableCommands(user);
            pagingService.listItems(availableCommands, page);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
