package it.unibo.ds.auth;

import java.util.Objects;

public class Credentials {
    private String userId;
    private String password;

    public Credentials(String id, String password) {
        this.userId = id;
        this.password = password;
    }

    public Credentials(Credentials other) {
        this.userId = other.userId;
        this.password = other.password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(userId, that.userId) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, password);
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
