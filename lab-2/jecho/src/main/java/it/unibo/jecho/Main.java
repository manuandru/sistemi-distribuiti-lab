package it.unibo.jecho;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    private final static Option upperCase = Option.builder("u").longOpt("uppercase").desc("Echo is upper case").build();

    private final static Option lowerCase = Option.builder("l").longOpt("lowercase").desc("Echo is lower case").build();

    private final static Options options = new Options();

    static {
        options.addOption(upperCase);
        options.addOption(lowerCase);
    }

    public static void main(String... args) {
        var parser = new DefaultParser();

        try {
            var opts = parser.parse(options, args);
            var mode = opts.hasOption("uppercase") ? Mode.UPPERCASE
                    : opts.hasOption("lowercase") ? Mode.LOWERCASE 
                        : Mode.NORMAL;

            var username = System.getenv("JECHO_USER");

            if (username != null) {
                printLine("Hello " + username + ", write your lines here:", mode);
            } else {
                printLine("Hello, write your lines here:", mode);
            }
            
            var reader = new BufferedReader(new InputStreamReader(System.in));
            reader.lines().forEach(line -> printLine(line, mode));            
        } catch (ParseException e) {
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void printLine(String line, Mode mode) {
        switch (mode) {
            case NORMAL:
                System.out.println(line);
                break;
            case LOWERCASE:
                System.out.println(line.toLowerCase());
                break;
            case UPPERCASE:
                System.out.println(line.toUpperCase());
                break;
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        System.out.print("> ");
        System.out.flush();
    }

}
