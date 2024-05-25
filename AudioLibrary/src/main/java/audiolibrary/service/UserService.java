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

    public User getCurrentUser(){
        return currentState.getUser();
    }

    public boolean isCurrentUserAdmin() {
        return currentState instanceof AdminUser;
    }

    public boolean isCurrentUserAuthenticated() {
        return (currentState instanceof AuthenticatedUser) || (currentState instanceof AdminUser);
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

    /**
     * Register a new user with the given username and password and add it to the database
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the user was created successfully
     * @throws UsernameAlreadyExistsException if the username already exists
     */
    public boolean performregisterUser(String username, String password) throws UsernameAlreadyExistsException {
        if (userDAO.findUserByUsername(username) != null) {
            throw new UsernameAlreadyExistsException(); // Username already exists
        }
        User user = new User(0, username, password, false);
        return userDAO.createUser(user);
    }

    /**
     * Logs in a user with the given username and password and sets the current
     * state to AuthenticatedUser/AdminUser depending on the user's role
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the user was logged in successfully
     * @throws InvalidUsernameOrPasswordException if the username or password is incorrect
     */
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

    /**
     * Logs out the current user by setting the current state to AnonymousUser
     */
    public void performlogoutUser() {
        setCurrentState(new AnonymousUser(this));
    }

    /**
     * Promotes a user to an admin by setting the admin flag to true in the database
     * @param username the username of the user to promote
     * @return true if the user was promoted successfully
     */
    public boolean performpromoteUser(String username) {
        User user = userDAO.findUserByUsername(username);
        if (user != null && !user.isAdmin()) {
            user.setAdmin(true);
            return userDAO.updateUser(user);
        }
        throw new IllegalArgumentException("User not found or is already an admin");
    }

}