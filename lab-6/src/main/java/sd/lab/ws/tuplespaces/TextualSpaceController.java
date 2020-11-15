package sd.lab.ws.tuplespaces;

import io.javalin.Javalin;
import io.javalin.http.Context;
import sd.lab.ws.tuplespaces.impl.TextualSpaceControllerImpl;

public interface TextualSpaceController {

    String path();

    void getAll(Context context) throws Exception;

    void get(Context context) throws Exception;

    void delete(Context context) throws Exception;

    void post(Context context) throws Exception;

    String path(String subPath);

    void registerRoutes(Javalin app);

    static TextualSpaceController of(String root) {
        return new TextualSpaceControllerImpl(root);
    }
}
