package audiolibrary.model;

public interface UserState {
    boolean register(String username, String password) throws Exception;
    boolean login(String username, String password) throws Exception;
    void logout() throws Exception;
    boolean promote(String username) throws Exception;
    User getUser() throws Exception;
}
