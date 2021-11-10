package it.unibo.ds.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalAuthenticator implements Authenticator {

    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public synchronized void register(User user) throws ConflictException {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Invalid username: " + user.getUsername());
        }
        if (user.getEmailAddresses().isEmpty()) {
            throw new IllegalArgumentException("No email provided for user: " + user.getUsername());
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("No password provided for user: " + user.getUsername());
        }
        if (usersByUsername.containsKey(user.getUsername())) {
            throw new ConflictException("Username already exists: " + user.getUsername());
        }
        for (var email : user.getEmailAddresses()) {
            if (usersByEmail.containsKey(email)) {
                throw new ConflictException("Email address already taken: " + email);
            }
        }
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        var toBeAdded = new User(user); // defensive copy
        usersByUsername.put(user.getUsername(), toBeAdded);
        for (var email : user.getEmailAddresses()) {
            usersByEmail.put(email, toBeAdded);
        }
    }

    @Override
    public synchronized Token authorize(Credentials credentials) throws WrongCredentialsException {
        if (credentials.getUserId() == null || credentials.getUserId().isBlank()) {
            throw new IllegalArgumentException("Missing user ID: " + credentials.getUserId());
        }
        if (credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            throw new IllegalArgumentException("Missing password: " + credentials.getPassword());
        }
        try {
            String userId = credentials.getUserId();
            User user = findUser(userId);
            if (!credentials.getPassword().equals(user.getPassword())) {
                throw new WrongCredentialsException("Wrong credentials for user: " + userId);
            }
            return new Token(user.getUsername(), user.getRole());
        } catch (MissingException e) {
            throw new WrongCredentialsException(e.getMessage());
        }
    }

    @Override
    public synchronized void remove(String userId) throws MissingException {
        User user = get(userId);
        usersByUsername.remove(user.getUsername());
        for (var email : user.getEmailAddresses()) {
            usersByEmail.remove(email);
        }
    }

    private User findUser(String userId) throws MissingException {
        User user = usersByUsername.getOrDefault(userId, usersByEmail.get(userId));
        if (user == null) {
            throw new MissingException("No such a user: " + userId);
        }
        return user;
    }

    private User hidePassword(User user) {
        var copy = new User(user); // defensive copy
        copy.setPassword(null);
        return copy;
    }

    @Override
    public synchronized User get(String userId) throws MissingException {
        return hidePassword(findUser(userId));
    }

    @Override
    public synchronized Set<? extends User> getAll() {
        return usersByUsername.values().stream().map(this::hidePassword).collect(Collectors.toSet());
    }

    private void checkForConflictInEdit(User oldUser, User newUser) throws ConflictException {
        if (!oldUser.getUsername().equals(newUser.getUsername()) && usersByUsername.containsKey(newUser.getUsername())) {
            throw new ConflictException("Username %s already taken: " + newUser.getUsername());
        }
        var oldEmails = Set.copyOf(oldUser.getEmailAddresses());
        for (var newEmail : newUser.getEmailAddresses()) {
            if (!oldEmails.contains(newEmail) && usersByEmail.containsKey(newEmail)) {
                throw new ConflictException("Email address %s already taken: " + newEmail);
            }
        }
    }

    @Override
    public synchronized void edit(String userId, User changes) throws MissingException, ConflictException {
        var oldUser = findUser(userId);
        checkForConflictInEdit(oldUser, changes);
        remove(userId);
        register(changes);
    }
}
