package audiolibrary.commands;

import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.service.UserService;

public class CommandController {
    private UserService userService;

    public CommandController(UserService userService) {
        this.userService = userService;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" ");
        switch (parts[0]) {
            case "register":
                try {
                    if (parts.length != 3) throw new InvalidNumberOfArgumentsException();
                    userService.registerUser(parts[1], parts[2]);
                    System.out.println("Registered successfully");
                } catch (InvalidNumberOfArgumentsException e){
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Register failed: " + e.getMessage());
                }
                break;
            case "login":
                try {
                    if (parts.length != 3) throw new InvalidNumberOfArgumentsException();
                    userService.loginUser(parts[1], parts[2]);
                    System.out.println("Login successful");
                } catch (InvalidNumberOfArgumentsException e){
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Login failed: " + e.getMessage());
                }
                break;
            case "logout":
                try {
                    if (parts.length != 1) throw new InvalidNumberOfArgumentsException();
                    userService.logoutUser();
                    System.out.println("Logout successful");
                } catch (InvalidNumberOfArgumentsException e){
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Logout failed: " + e.getMessage());
                }
                break;
            case "promote":
                try {
                    if (parts.length != 2) throw new InvalidNumberOfArgumentsException();
                    userService.promoteUser(parts[1]);
                    System.out.println("User promoted successfully");
                } catch (InvalidNumberOfArgumentsException e){
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Promotion failed: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Command unknown!");
        }
    }
}
