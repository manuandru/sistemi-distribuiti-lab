package it.unibo.ds.auth;

import java.util.Set;

public interface Authenticator {
    void register(User user) throws ConflictException;

    Token authorize(Credentials credentials) throws WrongCredentialsException;

    void remove(String userId) throws MissingException;

    User get(String userId) throws MissingException;

    void edit(String userId, User changes) throws MissingException, ConflictException;

    Set<? extends User> getAll();
}
