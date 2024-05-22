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

        while (true) {
            System.out.print(">");
            String command = scanner.nextLine();
            String[] parts = command.split(" ");

            switch (parts[0]) {
                case "register":
                    try {
                        userService.registerUser(parts[1], parts[2]);
                        System.out.println("Registered successfully");
                    } catch (Exception e) {
                        System.out.println("Register failed: " + e.getMessage());
                    }
                    break;
                case "login":
                    try {
                        userService.loginUser(parts[1], parts[2]);
                        System.out.println("Login successful");
                    } catch (Exception e) {
                        System.out.println("Login failed: " + e.getMessage());
                    }
                    break;
                case "logout":
                    try {
                        userService.logoutUser();
                        System.out.println("Logout successful");
                    } catch (Exception e) {
                        System.out.println("Logout failed: " + e.getMessage());
                    }
                    break;
                case "promote":
                    try {
                        userService.promoteUser(parts[1]);
                        System.out.println("User promoted successfully");
                    } catch (Exception e) {
                        System.out.println("Promotion failed: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Command unknown!");
            }
        }
    }
}
