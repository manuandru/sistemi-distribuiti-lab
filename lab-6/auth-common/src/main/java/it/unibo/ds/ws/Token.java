package it.unibo.ds.ws;

import java.util.Objects;

public class Token {
    private String username;
    private Role role;

    public Token(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public Token(Token other) {
        this.username = other.username;
        this.role = other.role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(username, token.username) && role == token.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, role);
    }

    @Override
    public String toString() {
        return "Token{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
