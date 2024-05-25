package audiolibrary.model;

public class Audit {
    private String username;
    private String action;
    private boolean success;

    public Audit(String username, String action, boolean success) {
        this.username = username;
        this.action = action;
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return "User: " + username + ", Action: " + action + ", Success: " + success;
    }
}
