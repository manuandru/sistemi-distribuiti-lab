package sd.lab.ws;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.swagger.v3.oas.models.info.Info;
import it.unibo.ds.ws.Authenticator;
import it.unibo.ds.ws.LocalAuthenticator;
import sd.lab.ws.tokens.TokenController;
import sd.lab.ws.users.UserController;
import sd.lab.ws.utils.Filters;

public class AuthService {

    private static final String API_VERSION = "0.1.0";
    private static final int DEFAULT_PORT = 10000;

    private final int port;
    private final Javalin server;
    private final Authenticator localAuthenticator = new LocalAuthenticator();

    public AuthService(int port) {
        this.port = port;
        server = Javalin.create(config -> {
            config.enableDevLogging();
            config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
        });

        server.before(path("/*"), Filters.putSingletonInContext(Authenticator.class, localAuthenticator));
        UserController.of(path("/users")).registerRoutes(server);
        TokenController.of(path("/tokens")).registerRoutes(server);
    }

    public static void main(String[] args) {
        new AuthService(args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT).start();
    }

    public void start() {
        server.start(port);
    }

    public void stop() {
        server.stop();
    }

    private static String path(String subPath) {
        return "/auth/v" + API_VERSION + subPath;
    }

    private static OpenApiOptions getOpenApiOptions() {
        Info applicationInfo = new Info()
                .version(API_VERSION)
                .description("Auth Service");
        return new OpenApiOptions(applicationInfo).path(path("/index*"));
    }
}
