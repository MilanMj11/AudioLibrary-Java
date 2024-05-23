import audiolibrary.commands.CommandController;
import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.exceptions.InvalidUsernameOrPasswordException;
import audiolibrary.exceptions.UsernameAlreadyExistsException;
import audiolibrary.service.PlaylistService;
import audiolibrary.service.SongService;
import audiolibrary.service.UserService;
import audiolibrary.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        /// docker exec -it mysql-container mysql -u root -p
        /// show databases
        /// use audiolibrary
        /// select * from users;

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        SongService songService = new SongService();
        PlaylistService playlistService = new PlaylistService();
        CommandController commandController = new CommandController(userService, songService, playlistService);

        while (true) {
            System.out.print(">");
            String command = scanner.nextLine();
            commandController.executeCommand(command);
        }
    }
}
