package audiolibrary.commands;

import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.service.AuditService;
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
    private AuditService auditService;

    public CommandController(UserService userService, SongService songService, PlaylistService playListService, AuditService auditService){
        this.userService = userService;
        this.songService = songService;
        this.playlistService = playListService;
        this.auditService = auditService;
    }

    public void executeCommand(String command) {
        String[] parts = splitCommand(command);
        if (parts.length == 0) {
            System.out.println("Command unknown!");
            return;
        }
        boolean succesfulCommand = true;
        switch (parts[0]) {
            case "register":
                try {
                    if (parts.length != 3) throw new InvalidNumberOfArgumentsException();
                    userService.registerUser(parts[1], parts[2]);
                    System.out.println("Registered successfully");
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    succesfulCommand = false;
                    System.out.println("Register failed: " + e.getMessage());
                }
                break;
            case "login":
                try {
                    if (parts.length != 3) throw new InvalidNumberOfArgumentsException();
                    userService.loginUser(parts[1], parts[2]);
                    System.out.println("Login successful");
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    succesfulCommand = false;
                    System.out.println("Login failed: " + e.getMessage());
                }
                break;
            case "logout":
                try {
                    if (parts.length != 1) throw new InvalidNumberOfArgumentsException();
                    userService.logoutUser();
                    System.out.println("Logout successful");
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    succesfulCommand = false;
                    System.out.println("Logout failed: " + e.getMessage());
                }
                break;
            case "promote":
                try {
                    if (parts.length != 2) throw new InvalidNumberOfArgumentsException();
                    userService.promoteUser(parts[1]);
                    System.out.println("User promoted successfully");
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    succesfulCommand = false;
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
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    /// Other exceptions
                    succesfulCommand = false;
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
                    succesfulCommand = false;
                } catch (Exception e) {
                    succesfulCommand = false;
                    System.out.println("Add failed: " + e.getMessage());
                }
                break;
            case "list":
                try {
                    if (parts.length == 2) {
                        if ("playlists".equals(parts[1])) {
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
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    succesfulCommand = false;
                    System.out.println("List failed: " + e.getMessage());
                }
                break;
            case "search":
                try {
                    if (parts.length == 3) {

                        String searchCriteria = parts[1];
                        String searchTerm = parts[2];

                        searchTerm = getRidOfQuotes(searchTerm);
                        searchTerm = searchTerm.trim();

                        // List<Song> songs = songService.searchSongs(searchCriteria, searchTerm);
                        songService.listSongs(songService.searchSongs(searchCriteria, searchTerm), 1);
                    } else if (parts.length == 4) {

                        String searchCriteria = parts[1];
                        String searchTerm = parts[2];
                        int page = Integer.parseInt(parts[3]);

                        searchTerm = getRidOfQuotes(searchTerm);
                        searchTerm = searchTerm.trim();

                        songService.listSongs(songService.searchSongs(searchCriteria, searchTerm), page);
                    } else {
                        throw new InvalidNumberOfArgumentsException();
                    }
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    succesfulCommand = false;
                    System.out.println("Search failed: " + e.getMessage());
                }
                break;
            case "export":
                try {
                    if (parts.length >= 2 && "playlist".equals(parts[1])) {

                        if (parts.length != 4) {
                            throw new InvalidNumberOfArgumentsException();
                        }
                        if (!userService.isCurrentUserAuthenticated()) {
                            throw new Exception("You need to be logged in to export a playlist.");
                        }

                        String playlistName = parts[2];
                        playlistName = getRidOfQuotes(playlistName);

                        String format = parts[3];
                        playlistService.exportPlaylist(userService.getCurrentUser(), playlistName, format);
                    } else {
                        throw new Exception("Unknown export command");
                    }
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    succesfulCommand = false;
                    System.out.println("Export failed: " + e.getMessage());
                }
                break;
            case "audit":
                try {
                    if (parts.length == 2){
                        if (!userService.isCurrentUserAdmin()){
                            throw new Exception("Only admins can list audit entries.");
                        }
                        auditService.listAuditEntries(parts[1], 1);
                    } else if(parts.length == 3){
                        if (!userService.isCurrentUserAdmin()){
                            throw new Exception("Only admins can list audit entries.");
                        }
                        auditService.listAuditEntries(parts[1], Integer.parseInt(parts[2]));
                    }
                    else {
                        succesfulCommand = false;
                        throw new InvalidNumberOfArgumentsException();
                    }
                } catch (InvalidNumberOfArgumentsException e) {
                    succesfulCommand = false;
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    succesfulCommand = false;
                    System.out.println("Audit failed: " + e.getMessage());
                }
                break;
            case "exit":
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                succesfulCommand = false;
                System.out.println("Command unknown!");
        }
        if (userService.isCurrentUserAuthenticated())
            try {
                auditService.logCommand(userService.getCurrentUser(), command, succesfulCommand);
            } catch (Exception e) {
                System.out.println("Failed to log command: " + e.getMessage());
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
