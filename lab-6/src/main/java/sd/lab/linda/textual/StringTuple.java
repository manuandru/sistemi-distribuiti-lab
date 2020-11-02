package sd.lab.linda.textual;

import sd.lab.linda.core.Tuple;

import java.util.Objects;

public final class StringTuple implements Tuple {

    public static StringTuple of(String regex) {
        return new StringTuple(regex);
    }
    
    private final String value;
    
    private StringTuple(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringTuple that = (StringTuple) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public String getValue() {
        return value;
    }
}
