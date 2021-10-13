package sd.lab.concurrency;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourcesUtils {
    public static InputStream openResource(String resource) {
        var file = ResourcesUtils.class.getResource(resource);
        try {
            return Objects.requireNonNull(file.openStream(), "");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot access to resource " + resource, e);
        }
    }
}
