package audiolibrary.model;

import audiolibrary.service.UserService;

public class AnonymousUser implements UserState{
    private UserService userService;

    public AnonymousUser(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean register(String username, String password) throws Exception {
        return userService.performregisterUser(username, password);
    }

    @Override
    public boolean login(String username, String password) throws Exception {
        return userService.performloginUser(username, password);
    }

    @Override
    public void logout() throws Exception {
        throw new Exception("You are not logged in.");
    }

    @Override
    public boolean promote(String username) throws Exception {
        throw new Exception("Only admins can promote users.");
    }

    @Override
    public User getUser(){
        return null;
    }
}
