package it.unibo.ds.auth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class User {
    private String fullName;
    private String username;
    private String password;
    private LocalDate birthDate;
    private final List<String> emailAddresses;
    private Role role;

    public User(String fullName, String username, String password, LocalDate birthDate, Role role, String... emails) {
        this(fullName, username, password, birthDate, role, Arrays.asList(emails));
    }

    public User(String fullName, String username, String password, LocalDate birthDate, Role role, List<String> emails) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
        this.emailAddresses = emails == null ? new ArrayList<>() : new ArrayList<>(emails);
    }

    public User(User other) {
        this.fullName = other.fullName;
        this.username = other.username;
        this.password = other.password;
        this.birthDate = other.birthDate;
        this.role = other.role;
        this.emailAddresses = new ArrayList<>(other.emailAddresses);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
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
        User user = (User) o;
        return Objects.equals(fullName, user.fullName) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(birthDate, user.birthDate) && Objects.equals(emailAddresses, user.emailAddresses) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, username, password, birthDate, emailAddresses, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", emailAddresses=" + emailAddresses +
                '}';
    }
}
