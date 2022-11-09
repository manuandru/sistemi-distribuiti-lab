package it.unibo.ds.lab.ws;

import it.unibo.ds.ws.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractTestAuthenticator {

    private final User giovanni = new User(
            "Giovanni Ciatto",
            "gciatto",
            "password.",
            LocalDate.of(1992, Month.JANUARY, 1),
            Role.USER,
            "giovanni.ciatto@unibo.it",
            "giovanni.ciatto@studio.unibo.it"
    );

    private final User andrea = new User(
            "Andrea Omicini",
            "aomicini",
            "123456!",
            LocalDate.of(1965, Month.FEBRUARY, 2),
            Role.USER,
            "andrea.omicini@unibo.it"
    );

    private final User stefano = new User(
            null,
            "stemar",
            "987abc!",
            null,
            null,
            "stefano.mariani@unibo.it"
    );

    private final User noUser = new User(
            null,
            null,
            "987abc!",
            null,
            null
    );

    private final User noPassword = new User(
            null,
            "someone",
            null,
            null,
            null
    );

    private final User noEmail = new User(
            null,
            "someone",
            "password",
            null,
            null
    );

    private Authenticator authenticator;

    @BeforeEach
    public final void setup() throws ConflictException, IOException {
        beforeCreatingAuthenticator();
        authenticator = createAuthenticator();

        authenticator.register(giovanni);
        authenticator.register(andrea);
        authenticator.register(stefano);
    }

    protected abstract void beforeCreatingAuthenticator() throws IOException;

    protected abstract Authenticator createAuthenticator() throws ConflictException;

    @AfterEach
    public final void teardown() throws InterruptedException {
        shutdownAuthenticator(authenticator);
        afterShuttingAuthenticatorDown();
    }

    protected abstract void shutdownAuthenticator(Authenticator authenticator);

    protected abstract void afterShuttingAuthenticatorDown() throws InterruptedException;

    public void testRegisterErrors() {
        assertThrows(ConflictException.class, () -> authenticator.register(andrea));
        assertThrows(ConflictException.class, () -> authenticator.register(giovanni));
        assertThrows(ConflictException.class, () -> authenticator.register(stefano));
        assertThrows(IllegalArgumentException.class, () -> authenticator.register(noUser));
        assertThrows(IllegalArgumentException.class, () -> authenticator.register(noPassword));
        assertThrows(IllegalArgumentException.class, () -> authenticator.register(noEmail));
    }

    private static Credentials credentialsOf(User user) {
        return new Credentials(user.getUsername(), user.getPassword());
    }

    private static List<Credentials> allCredentialsOf(User user) {
        return Stream.concat(
                Stream.of(user.getUsername()),
                user.getEmailAddresses().stream()
        ).map(it -> new Credentials(it, user.getPassword())).collect(Collectors.toList());
    }

    private static Token tokenOf(User user) {
        return new Token(user.getUsername(), Optional.ofNullable(user.getRole()).orElse(Role.USER));
    }

    public void testAuthorize() throws WrongCredentialsException {
        for (var user : List.of(giovanni, andrea, stefano)) {
            for (var credentials : allCredentialsOf(user)) {
                assertEquals(tokenOf(user), authenticator.authorize(credentials));
            }

            var user2 = new User(user);
            user2.setUsername(user.getUsername() + "2");
            assertThrows(WrongCredentialsException.class, () -> authenticator.authorize(credentialsOf(user2)));

            var user3 = new User(user);
            user3.setPassword(user.getPassword() + "-");
            assertThrows(WrongCredentialsException.class, () -> authenticator.authorize(credentialsOf(user3)));
        }
        assertThrows(IllegalArgumentException.class, () -> authenticator.authorize(credentialsOf(noUser)));
        assertThrows(IllegalArgumentException.class, () -> authenticator.authorize(credentialsOf(noPassword)));
        assertThrows(WrongCredentialsException.class, () -> authenticator.authorize(credentialsOf(noEmail)));
    }

}
