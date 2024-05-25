# Audio Library in Java

## Overview

The Audio Library is a Java-based application designed to manage songs and playlists for users. It supports functionalities such as user registration, authentication, song and playlist management, export functionality, and an audit logging system. 
The application follows a command-based interface to interact with the system.

## Features

### Real-Time Information
- **Get help: List the avaialable commands:** `help` 

### User Management
- **Register a new user:** `register <username> <password>`
- **Login as a user:** `login <username> <password>`
- **Logout the current user:** `logout`
- **Promote a user to admin:** `promote <username>`

### Song Management
- **Create a new song:** `create song "<name>" "<author>" <releaseYear>`
- **Search for songs:** `search <name/author> "<term>" <page>`

### Playlist Management
- **Create a new playlist:** `create playlist "<name>"`
- **Add songs to a playlist:** `add "<playlistName>" [<songId1>,<songId2>,...]`
- **List all playlists:** `list playlists <page>`
- **Export a playlist to JSON:** `export playlist "<playlistName>" json`

### Audit Logging
- **View audit logs:** `audit <username> <page>`
  - Only admins can view audit logs.

## Custom Format for Audit Logs
Audit logs are saved in a custom format:
`username|command|success`


## How to Use

### Execute Commands
Commands can be executed through the command-line interface. Each command follows a specific syntax and provides feedback based on its success or failure.

### Exporting Playlists
Playlists can be exported in JSON format. The exported file will be named as follows:
`export_<username><playlistName><date>.json`

### Audit Logs
All executed commands are logged with the user information, command, and success status. Admin users can view these logs using the `audit` command.

## Data Storage

### User Database
User information is stored in a SQL database using Docker. The application interacts with the database to perform user management operations such as registration, authentication, and user promotion.

### Song Data
Song data is stored in a CSV file. The application reads song information from the CSV file to provide functionalities such as creating songs, searching, and listing songs.

### Playlist Data
Playlist data is stored in JSON format. Each playlist is represented as a JSON file containing the playlist's name and the IDs of the songs it contains. The application exports playlists to JSON files using this format.

## Code Structure

### CommandController
The `CommandController` class handles the execution of commands. It parses the user input and forwards the commands to the appropriate services.

### Services
- `UserService` handles user-related functionalities.
- `SongService` manages song-related operations.
- `PlaylistService` manages playlist-related operations and exports.
- `AuditService` logs commands and retrieves audit logs.

### PagingService
The `PagingService` class provides pagination functionality for listing songs, playlists, and audit logs.

### Helper
A singleton `Helper` class provides utility functions, such as displaying available commands.

### Exception Handling
Custom exceptions are used to handle invalid arguments and other error scenarios.

