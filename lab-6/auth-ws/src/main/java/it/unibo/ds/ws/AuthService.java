package it.unibo.ds.ws;

import io.javalin.Javalin;
import io.javalin.openapi.OpenApiInfo;
import io.javalin.openapi.plugin.OpenApiConfiguration;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.plugin.bundled.RouteOverviewPlugin;
import it.unibo.ds.ws.tokens.TokenController;
import it.unibo.ds.ws.users.UserController;
import it.unibo.ds.ws.utils.Filters;
import it.unibo.ds.ws.utils.Plugins;

public class AuthService {

    private static final String API_VERSION = "0.1.0";

    public static final String BASE_URL = "/auth/v" + API_VERSION;

    private static final int DEFAULT_PORT = 10000;

    private final int port;
    private final Javalin server;
    private final Authenticator localAuthenticator = new LocalAuthenticator();

    public AuthService(int port) {
        this.port = port;
        server = Javalin.create(config -> {
            config.plugins.enableDevLogging();
            config.jsonMapper(new JavalinGsonAdapter(GsonUtils.createGson()));
            config.plugins.register(Plugins.openApiPlugin(API_VERSION, "/doc"));
            config.plugins.register(Plugins.swaggerPlugin("/doc", "/ui"));
            config.plugins.register(Plugins.routeOverviewPlugin("/routes"));
        });

        server.before(path("/*"), Filters.putSingletonInContext(Authenticator.class, localAuthenticator));

        UserController.of(path("/users")).registerRoutes(server);
        TokenController.of(path("/tokens")).registerRoutes(server);
    }

    public void start() {
        server.start(port);
    }

    public void stop() {
        server.stop();
    }

    private static String path(String subPath) {
        return BASE_URL + subPath;
    }

    public static void main(String[] args) {
        new AuthService(args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT).start();
    }
}
