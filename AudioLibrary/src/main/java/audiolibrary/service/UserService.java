package audiolibrary.service;

import audiolibrary.dao.UserDAO;
import audiolibrary.exceptions.InvalidNumberOfArgumentsException;
import audiolibrary.exceptions.InvalidUsernameOrPasswordException;
import audiolibrary.exceptions.UsernameAlreadyExistsException;
import audiolibrary.model.*;

public class UserService {
    private UserDAO userDAO;
    private UserState currentState;

    public UserService() {
        this.userDAO = new UserDAO();
        this.currentState = new AnonymousUser(this);
    }

    public void setCurrentState(UserState state) {
        this.currentState = state;
    }

    public boolean registerUser(String username, String password) throws Exception {
        return currentState.register(username, password);
    }

    public boolean loginUser(String username, String password) throws Exception {
        return currentState.login(username, password);
    }

    public void logoutUser() throws Exception {
        currentState.logout();
    }

    public boolean promoteUser(String username) throws Exception {
        return currentState.promote(username);
    }

    public boolean performregisterUser(String username, String password) throws UsernameAlreadyExistsException {
        if (userDAO.findUserByUsername(username) != null) {
            throw new UsernameAlreadyExistsException(); // Username already exists
        }
        User user = new User(0, username, password, false);
        return userDAO.createUser(user);
    }

    public boolean performloginUser(String username, String password) throws InvalidUsernameOrPasswordException{
        User user = userDAO.findUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            if(user.isAdmin()){
                setCurrentState(new AdminUser(this, user));
            } else {
                setCurrentState(new AuthenticatedUser(this, user));
            }
            return true;
        }
        throw new InvalidUsernameOrPasswordException();
    }

    public void performlogoutUser() {
        setCurrentState(new AnonymousUser(this));
    }

    public boolean performpromoteUser(String username) {
        User user = userDAO.findUserByUsername(username);
        if (user != null && user.isAdmin()) {
            user.setAdmin(true);
            return userDAO.updateUser(user);
        }
        throw new IllegalArgumentException("User not found or is already an admin");
    }

}