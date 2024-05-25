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

    public void listAuditEntries(String username, int page) throws Exception{
        List<Audit> userEntries = getAuditEntriesByUsername(username);
        if(userEntries.isEmpty()){
            System.out.println("No audit entries found for user " + username);
            return;
        }
        pagingService.listItems(userEntries, page);
    }

}
