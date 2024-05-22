import audiolibrary.commands.CommandController;
import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.exceptions.InvalidUsernameOrPasswordException;
import audiolibrary.exceptions.UsernameAlreadyExistsException;
import audiolibrary.service.UserService;
import audiolibrary.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();
        CommandController commandController = new CommandController(userService);

        while (true) {
            System.out.print(">");
            String command = scanner.nextLine();
            commandController.executeCommand(command);
        }
    }
}
