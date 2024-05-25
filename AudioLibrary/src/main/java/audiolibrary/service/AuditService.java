package audiolibrary.service;

import audiolibrary.model.Audit;
import audiolibrary.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AuditService {
    private static final String AUDIT_FILE = "audit.log";
    private PagingService<Audit> pagingService = new PagingService<>();

    /**
     * Logs a command to the audit log
     * @param user the user that executed the command
     * @param command the command that was executed
     * @param success whether the command was successful or not (went through or caught an exception)
     */
    public static void logCommand(User user, String command, boolean success){
        Audit auditEntry = new Audit(user.getUsername(), command, success);
        saveAuditEntry(auditEntry);
    }

    private static void saveAuditEntry(Audit entry) {
        try (FileWriter writer = new FileWriter(AUDIT_FILE, true);
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write(entryToCustomFormat(entry));
            bw.newLine();
        } catch (Exception e) {
            System.out.println("Failed to save audit log: " + e.getMessage());
        }
    }

    private static String entryToCustomFormat(Audit entry) {
        return entry.getUsername() + "|" + entry.getAction() + "|" + entry.isSuccess();
    }

    private static Audit entryFromCustomFormat(String entry) {
        String[] parts = entry.split("\\|");
        return new Audit(parts[0], parts[1], Boolean.parseBoolean(parts[2]));
    }

    /**
     * Loads all the audit entries from the audit log
     * @return a list of all the audit entries
     */
    public static List<Audit> loadAuditEntries() {
        List<Audit> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(AUDIT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                entries.add(entryFromCustomFormat(line));
            }
        } catch (Exception e) {
            System.out.println("Failed to load audit log: " + e.getMessage());
        }
        return entries;
    }

    /**
     * Gets all the audit entries for a specific user
     * @param username the username of the user that we want to get the audit entries for
     * @return a list of all the audit entries for the user
     */
    public static List<Audit> getAuditEntriesByUsername(String username) {
        List<Audit> allEntries = loadAuditEntries();
        List<Audit> userEntries = new ArrayList<>();
        for (Audit entry : allEntries) {
            if (entry.getUsername().equals(username)) {
                userEntries.add(entry);
            }
        }
        return userEntries;
    }

    /**
     * Lists all the audit entries for a specific user using paging
     * @param username the username of the user that we want to get the audit entries for
     * @param page the page number that we want to display
     * @throws Exception if the user has no audit entries yet
     */
    public void listAuditEntries(String username, int page) throws Exception{
        List<Audit> userEntries = getAuditEntriesByUsername(username);
        if(userEntries.isEmpty()){
            System.out.println("No audit entries found for user " + username);
            return;
        }
        pagingService.listItems(userEntries, page);
    }

}
