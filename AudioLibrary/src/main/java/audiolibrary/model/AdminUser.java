package audiolibrary.model;

import audiolibrary.service.UserService;

public class AdminUser implements UserState{
    private UserService userService;
    private User currentUser;

    public AdminUser(UserService userService, User currentUser) {
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
    public boolean promote(String username){
        return userService.performpromoteUser(username);
    }

}
