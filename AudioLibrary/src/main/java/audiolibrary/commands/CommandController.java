package audiolibrary.commands;

import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.service.PlaylistService;
import audiolibrary.service.SongService;
import audiolibrary.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandController {
    private UserService userService;
    private SongService songService;
    private PlaylistService playlistService;

    public CommandController(UserService userService, SongService songService, PlaylistService playListService) {
        this.userService = userService;
        this.songService = songService;
        this.playlistService = playListService;
    }

    public void executeCommand(String command) {
        String[] parts = splitCommand(command);
        if (parts.length == 0) {
            System.out.println("Command unknown!");
            return;
        }

        switch (parts[0]) {
            case "register":
                try {
                    if (parts.length != 3) throw new InvalidNumberOfArgumentsException();
                    userService.registerUser(parts[1], parts[2]);
                    System.out.println("Registered successfully");
                } catch (InvalidNumberOfArgumentsException e) {
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
                } catch (InvalidNumberOfArgumentsException e) {
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
                } catch (InvalidNumberOfArgumentsException e) {
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
                } catch (InvalidNumberOfArgumentsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Promotion failed: " + e.getMessage());
                }
                break;
            case "create":
                try {
                    boolean createdSomething = false;
                    /// This is the create song part ->
                    if (parts.length >= 2 && "song".equals(parts[1])) {
                        /// Only Admins can use the create command.
                        if (!userService.isCurrentUserAdmin()) {
                            throw new Exception("Only admins can create songs.");
                        }

                        if (parts.length != 5) throw new InvalidNumberOfArgumentsException();

                        String songName = parts[2];
                        String authorName = parts[3];

                        /// -> Song Name and Author Name need to respect the format "name"
                        if (!songName.startsWith("\"") || !songName.endsWith("\"") ||
                                !authorName.startsWith("\"") || !authorName.endsWith("\"")) {
                            throw new Exception("Song name and author name must be quoted with \"\".");
                        }

                        /// -> Getting rid of the quotes
                        songName = songName.substring(1, songName.length() - 1);
                        authorName = authorName.substring(1, authorName.length() - 1);

                        songService.createSong(songName, authorName, Integer.parseInt(parts[4]));
                        createdSomething = true;
                        System.out.println("Song created successfully");
                    }

                    // This is the create playlist part ->
                    if (parts.length >= 2 && "playlist".equals(parts[1])) {
                        if (!userService.isCurrentUserAuthenticated()) {
                            throw new Exception("You need to be logged in to create a playlist.");
                        }

                        if (parts.length != 3) throw new InvalidNumberOfArgumentsException();

                        String playlistName = parts[2];
                        playlistName = getRidOfQuotes(playlistName);
                        playlistService.createPlaylist(userService.getCurrentUser(), playlistName);
                        createdSomething = true;
                        // System.out.println("Playlist created successfully");
                    }
                    if (!createdSomething)
                        throw new Exception("Unknown create command");

                } catch (InvalidNumberOfArgumentsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    System.out.println("Create failed: " + e.getMessage());
                }
                break;
            case "add":
                try {
                    if (parts.length != 3) {
                        throw new InvalidNumberOfArgumentsException();
                    }
                    if (!userService.isCurrentUserAuthenticated()) {
                        throw new Exception("You need to be logged to add songs to a playlist.");
                    }

                    String playlistName = parts[1];
                    playlistName = getRidOfQuotes(playlistName);

                    List<Integer> songIds = Arrays.stream(parts[2].replace("[", "").replace("]", "").split(","))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList());
                    playlistService.addSongsToPlaylist(userService.getCurrentUser(), playlistName, songIds, songService.getAllSongs());

                } catch (InvalidNumberOfArgumentsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Add failed: " + e.getMessage());
                }
                break;
            case "list":
                try {
                    if (parts.length == 2){
                        if ("playlists".equals(parts[1])){
                            if (!userService.isCurrentUserAuthenticated()) {
                                throw new Exception("You need to be logged in to list your playlists.");
                            }
                            playlistService.listPlaylists(userService.getCurrentUser(), 1);
                        } else {
                            throw new Exception("Unknown list command");
                        }
                    } else {
                        if (parts.length == 3) {
                            if (!userService.isCurrentUserAuthenticated()) {
                                throw new Exception("You need to be logged in to list your playlists.");
                            }
                            playlistService.listPlaylists(userService.getCurrentUser(), Integer.parseInt(parts[2]));
                        } else {
                            throw new InvalidNumberOfArgumentsException();
                        }
                    }
                } catch (InvalidNumberOfArgumentsException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("List failed: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Command unknown!");
        }
    }

    private String getRidOfQuotes(String str) throws Exception {
        if (!str.startsWith("\"") || !str.endsWith("\"")) {
            throw new Exception("Song name and author name must be quoted with \"\".");
        }
        return str.substring(1, str.length() - 1);
    }

    private String[] splitCommand(String command) {
        Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
        Matcher matcher = pattern.matcher(command);
        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1));
            } else {
                parts.add(matcher.group(2));
            }
        }
        return parts.toArray(new String[0]);
    }

}
