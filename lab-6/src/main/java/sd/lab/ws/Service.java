package sd.lab.ws;

import io.javalin.Javalin;
import sd.lab.ws.tuplespaces.TextualSpaceController;

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
        var server = Javalin.create(config -> {
            config.enableDevLogging();
        }).start(port);

        TextualSpaceController.of(path("/tuple-spaces")).registerRoutes(server);

        return server;
    }

    private static String path(String subPath) {
        return "linda/v" + API_VERSION + subPath;
    }
}
