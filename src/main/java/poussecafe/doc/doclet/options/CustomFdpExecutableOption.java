package poussecafe.doc.doclet.options;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jdk.javadoc.doclet.Doclet.Option;
import poussecafe.doc.PousseCafeDocGenerationConfiguration;

import static java.util.Arrays.asList;

public class CustomFdpExecutableOption implements Option {

    public CustomFdpExecutableOption(PousseCafeDocGenerationConfiguration.Builder configBuilder) {
        Objects.requireNonNull(configBuilder);
        this.configBuilder = configBuilder;
    }

    private PousseCafeDocGenerationConfiguration.Builder configBuilder;

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Custom executable for GraphViz's fdp";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return asList("-customFdpExecutable");
    }

    @Override
    public String getParameters() {
        return "";
    }

    @Override
    public boolean process(String option,
            List<String> arguments) {
        configBuilder.customFdpExecutable(Optional.of(arguments.get(0)));
        return true;
    }
}
