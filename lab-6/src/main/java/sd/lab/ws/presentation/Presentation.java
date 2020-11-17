package sd.lab.ws.presentation;

import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.ws.presentation.impl.*;

import java.util.HashMap;
import java.util.Map;

public class Presentation {
    private static final Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
    private static final Map<Class<?>, Deserializer<?>> deserializers = new HashMap<>();

    static {
        serializers.put(StringTuple.class, new StringTupleSerializer());
        deserializers.put(StringTuple.class, new StringTupleDeserializer());
        serializers.put(RegexTemplate.class, new RegexTemplateSerializer());
        deserializers.put(RegexTemplate.class, new RegexTemplateDeserializer());
        serializers.put(String.class, new StringSerializer());
        deserializers.put(String.class, new StringDeserializer());
    }

    public static <T> Serializer<T> serializerOf(Class<T> klass) {
        if (!serializers.containsKey(klass)) {
            serializers.keySet().stream()
                    .filter(key -> key.isAssignableFrom(klass))
                    .map(serializers::get)
                    .findAny()
                    .map(klass::cast)
                    .orElseThrow(() -> new IllegalArgumentException("No available serializer for class: " + klass.getName()));
        }
        return (Serializer<T>) serializers.get(klass);
    }

    public static <T> Deserializer<T> deserializerOf(Class<T> klass) {
        if (!deserializers.containsKey(klass)) {
            deserializers.keySet().stream()
                    .filter(key -> key.isAssignableFrom(klass))
                    .map(deserializers::get)
                    .findAny()
                    .map(klass::cast)
                    .orElseThrow(() -> new IllegalArgumentException("No available deserializer for class: " + klass.getName()));
        }
        return (Deserializer<T>) deserializers.get(klass);
    }
}
