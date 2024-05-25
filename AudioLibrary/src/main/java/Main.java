import audiolibrary.commands.CommandController;
import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.exceptions.InvalidUsernameOrPasswordException;
import audiolibrary.exceptions.UsernameAlreadyExistsException;
import audiolibrary.service.AuditService;
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

        /**
         * We initialize all the Services needed to run the application.
         */
        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        SongService songService = new SongService();
        PlaylistService playlistService = new PlaylistService();
        AuditService auditService = new AuditService();
        CommandController commandController = new CommandController(userService, songService, playlistService, auditService);

        while (true) {
            System.out.print(">");
            String command = scanner.nextLine();
            commandController.executeCommand(command);
        }
    }
}
