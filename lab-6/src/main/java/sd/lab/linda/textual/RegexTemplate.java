package sd.lab.linda.textual;

import sd.lab.linda.core.Template;
import sd.lab.linda.core.Tuple;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegexTemplate implements Template {
    
    private final Pattern regex;

    public static RegexTemplate of(String regex) {
        return new RegexTemplate(regex);
    }

    @Override
    public boolean matches(final Tuple tuple) {
        if (tuple instanceof StringTuple) {
            return regex.matcher(((StringTuple) tuple).getValue()).matches();
        }
        
        return false;
    }
    
    private RegexTemplate(final String regex) {
        Objects.requireNonNull(regex);
        this.regex = Pattern.compile(regex, Pattern.MULTILINE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegexTemplate that = (RegexTemplate) o;
        return regex.equals(that.regex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex);
    }

    public Pattern getRegex() {
        return regex;
    }

    @Override
    public String toString() {
        return "/" + regex.pattern() + "/";
    }

}
