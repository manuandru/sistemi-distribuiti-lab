package it.unibo.ds.lab.ws;

import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.ConflictException;
import it.unibo.ds.ws.LocalAuthenticator;
import it.unibo.ds.ws.WrongCredentialsException;
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
}
