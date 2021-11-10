package sd.lab.ws.users;

import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.User;
import sd.lab.ws.users.impl.UserApiImpl;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface UserApi {

    CompletableFuture<Collection<? extends String>> getAllNames(int skip, int limit, String filter);

    CompletableFuture<String> registerUser(User user);

    CompletableFuture<User> getUser(String userId);

    CompletableFuture<Void> removeUser(String userId);

    CompletableFuture<String> editUser(String userId, User changes);

    static UserApi of(Authenticator storage) {
        return new UserApiImpl(storage);
    }
}
