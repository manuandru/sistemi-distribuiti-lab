package sd.lab.agency;

import java.util.Objects;

public final class AID {
    private final String local;
    private final String environment;

    public static AID full(String local, String environment) {
        return new AID(local, Objects.requireNonNull(environment));
    }

    public static AID local(String local) {
        return new AID(local, null);
    }

    public static AID parse(String input) {
        var parts = input.split("@");
        if (parts.length == 2) {
            return AID.full(parts[0], parts[1]);
        } else {
            return AID.local(input);
        }
    }

    private AID(String local, String environment) {
        this.local = Objects.requireNonNull(local);
        this.environment = environment;
    }

    public String getLocalName() {
        return local;
    }

    public String getEnvironmentName() {
        return environment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AID aid = (AID) o;
        return local.equals(aid.local) && environment.equals(aid.environment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(local, environment);
    }

    @Override
    public String toString() {
        return local + "@" + environment;
    }
}
