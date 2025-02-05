package poussecafe.doc.doclet;

import java.util.function.Consumer;
import javax.tools.Diagnostic.Kind;
import jdk.javadoc.doclet.Reporter;

public class Logger {

    private Logger() {

    }

    public static void setReporter(Reporter reporter) {
        Logger.reporter = reporter;
    }

    private static Reporter reporter;

    public static void debug(String message, Object... args) {
        log(message, text -> reporter().print(Kind.OTHER, text), args);
    }

    public static void warn(String message, Object... args) {
        log(message, text -> reporter().print(Kind.WARNING, text), args);
    }

    public static void error(String message, Object... args) {
        log(message, text -> reporter().print(Kind.ERROR, text), args);
    }

    public static void info(String message, Object...args) {
        log(message, text -> reporter().print(Kind.NOTE, text), args);
    }

    private static Reporter reporter() {
        if(reporter == null) {
            reporter = new Slf4jReporter();
            info("No reporter set, falling back to SLF4J");
        }
        return reporter;
    }

    private static void log(String message, Consumer<String> logger, Object...args) {
        String[] parts = message.split("\\{\\}");
        StringBuilder builder = new StringBuilder();
        int minPartsArgs = Math.min(args.length, parts.length);
        for(int i = 0; i < minPartsArgs; ++i) {
            builder.append(parts[i]);
            builder.append(args[i]);
        }
        for(int i = args.length; i < parts.length; ++i) {
            builder.append(parts[i]);
        }
        var formattedMessage = builder.toString();
        try {
            logger.accept(formattedMessage);
        } catch(Exception e) {
            System.err.println(
                String.format("Logger failed, falling back to console: %s", formattedMessage)
            );
        }
    }
}
