package sd.lab.linda.core;

public interface Tuple {
    default boolean matches(Template template) {
        return template.matches(this);
    }

    Object getValue();
}
