package sd.lab.ws.tuplespaces;

import sd.lab.linda.textual.TextualSpace;
import sd.lab.ws.tuplespaces.impl.TextualSpaceStorageImpl;

import java.util.Collection;

public interface TextualSpaceStorage {
    TextualSpace getByName(String name);

    Collection<? extends TextualSpace> getAll();

    static TextualSpaceStorage getInstance() {
        return TextualSpaceStorageImpl.INSTANCE;
    }
}
