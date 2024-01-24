package it.unibo.ds.lab.ws;

import it.unibo.ds.lab.ws.client.RemoteAuthenticator;
import it.unibo.ds.ws.*;
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

    @Override
    @Test
    public void testGet() throws MissingException {
        super.testGet();
    }

    @Override
    @Test
    public void testDelete() throws MissingException, ConflictException {
        super.testDelete();
    }

    @Override
    @Test
    public void testPut() throws MissingException, ConflictException {
        super.testPut();
    }

    @Override
    @Test
    public void testGetAll() throws MissingException, ConflictException {
        super.testGetAll();
    }
}
