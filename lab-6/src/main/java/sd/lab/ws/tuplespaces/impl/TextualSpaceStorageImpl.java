package sd.lab.ws.tuplespaces.impl;

import sd.lab.linda.textual.TextualSpace;
import sd.lab.ws.tuplespaces.TextualSpaceStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class TextualSpaceStorageImpl implements TextualSpaceStorage {

    public static TextualSpaceStorage INSTANCE = new TextualSpaceStorageImpl();

    private final Map<String, TextualSpace> textualSpaces = new HashMap<>();

    @Override
    public synchronized TextualSpace getByName(String name) {
        if (textualSpaces.containsKey(name)) {
            return textualSpaces.get(name);
        } else {
            var ts = TextualSpace.of(name, ForkJoinPool.commonPool());
            textualSpaces.put(name, ts);
            return ts;
        }
    }

    @Override
    public synchronized Collection<? extends TextualSpace> getAll() {
        return textualSpaces.values();
    }
}
