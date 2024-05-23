package audiolibrary.model;

import audiolibrary.service.UserService;

public class AuthenticatedUser implements UserState{
    private UserService userService;
    private User currentUser;

    public AuthenticatedUser(UserService userService, User currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @Override
    public boolean register(String username, String password) throws Exception {
        throw new Exception("You are already logged in.");
    }

    @Override
    public boolean login(String username, String password) throws Exception {
        throw new Exception("You are already logged in.");
    }

    @Override
    public void logout() throws Exception {
        userService.performlogoutUser();
    }

    @Override
    public boolean promote(String username) throws Exception {
        throw new Exception("Only admins can promote users.");
    }

    @Override
    public User getUser() {
        return currentUser;
    }
}
