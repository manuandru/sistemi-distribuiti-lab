package it.unibo.ds.auth;

import org.junit.jupiter.api.Test;

public class TestLocalAuthenticator extends AbstractTestAuthenticator {
    @Override
    protected void beforeCreatingAuthenticator() {
        // do nothing
    }

    @Override
    protected Authenticator createAuthenticator() throws ConflictException {
        return new LocalAuthenticator();
    }

    @Override
    protected void shutdownAuthenticator(Authenticator authenticator) {
        // do nothing
    }

    @Override
    protected void afterShuttingAuthenticatorDown() {
        // do nothing
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
