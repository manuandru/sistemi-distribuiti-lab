package sd.lab.utils;

@FunctionalInterface
public interface Action<E extends Exception> {
    void execute() throws E;
}