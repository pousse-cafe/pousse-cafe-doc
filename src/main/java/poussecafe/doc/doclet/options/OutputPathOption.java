package poussecafe.doc.doclet.options;

import java.util.List;
import java.util.Objects;
import jdk.javadoc.doclet.Doclet.Option;
import poussecafe.doc.PousseCafeDocGenerationConfiguration;

import static java.util.Arrays.asList;

public class OutputPathOption implements Option {

    public OutputPathOption(PousseCafeDocGenerationConfiguration.Builder configBuilder) {
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
        return "Ouptut directory";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return asList("-output");
    }

    @Override
    public String getParameters() {
        return "<path>";
    }

    @Override
    public boolean process(String option,
            List<String> arguments) {
        configBuilder.outputDirectory(arguments.get(0));
        return true;
    }
}
