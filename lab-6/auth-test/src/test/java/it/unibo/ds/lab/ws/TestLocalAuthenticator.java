package it.unibo.ds.lab.ws;

import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.ConflictException;
import it.unibo.ds.ws.LocalAuthenticator;

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
}
