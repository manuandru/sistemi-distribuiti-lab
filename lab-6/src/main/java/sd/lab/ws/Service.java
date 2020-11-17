package sd.lab.ws;

import io.javalin.Javalin;
import sd.lab.ws.tuplespaces.TextualSpaceController;
import sd.lab.ws.tuplespaces.TextualSpaceStorage;
import sd.lab.ws.utils.Filters;

import java.io.IOException;

public class Service {

    public static final int API_VERSION = 2;

    public static void main(String[] args) throws IOException {
        var service = startService(8080);

        while (System.in.read() >= 0) {
            // do nothing
        }
        service.stop();
        System.exit(0);
    }

    public static Javalin startService(int port) {
        var textualSpaceStorage = TextualSpaceStorage.getInstance();

        var server = Javalin.create(config -> {
//            config.server(() -> ServerUtils)
            config.enableDevLogging();
        }).start(port);

        server.before(Filters.putSingletonInContext(TextualSpaceStorage.class, textualSpaceStorage));

        TextualSpaceController.of(path("/tuple-spaces")).registerRoutes(server);

        return server;
    }

    private static String path(String subPath) {
        return "linda/v" + API_VERSION + subPath;
    }
}
