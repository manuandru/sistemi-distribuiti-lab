package it.unibo.ds.lab.ws;

import it.unibo.ds.lab.ws.client.RemoteAuthenticator;
import it.unibo.ds.ws.AuthService;
import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.ConflictException;
import it.unibo.ds.ws.WrongCredentialsException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestRemoteAuthenticator extends AbstractTestAuthenticator {

    private static final int port = 10000;

    private AuthService service;

    @Override
    protected void beforeCreatingAuthenticator() throws IOException {
        service = new AuthService(port);
        service.start();
    }

    @Override
    protected Authenticator createAuthenticator() throws ConflictException {
        return new RemoteAuthenticator("localhost", port);
    }

    @Override
    protected void shutdownAuthenticator(Authenticator authenticator) {
        // do nothing
    }

    @Override
    protected void afterShuttingAuthenticatorDown() {
        service.stop();
    }

    @Override
    @Test
    public void testRegisterErrors() {
        super.testRegisterErrors();
    }

    @Override
    @Test
    public void testAuthorize() throws WrongCredentialsException {
        super.testAuthorize();
    }
}
