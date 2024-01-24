package it.unibo.ds.ws;

import it.unibo.ds.ws.Authenticator;

public abstract class AbstractApi {
    private final Authenticator storage;

    public AbstractApi(Authenticator storage) {
        this.storage = storage;
    }

    public Authenticator storage() {
        return storage;
    }
}
