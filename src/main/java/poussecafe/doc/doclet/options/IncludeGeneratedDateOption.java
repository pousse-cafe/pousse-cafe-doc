package poussecafe.doc.doclet.options;

import java.util.List;
import java.util.Objects;
import jdk.javadoc.doclet.Doclet.Option;
import poussecafe.doc.PousseCafeDocGenerationConfiguration;

import static java.util.Arrays.asList;

public class IncludeGeneratedDateOption implements Option {

    public IncludeGeneratedDateOption(PousseCafeDocGenerationConfiguration.Builder configBuilder) {
        Objects.requireNonNull(configBuilder);
        this.configBuilder = configBuilder;
    }

    private PousseCafeDocGenerationConfiguration.Builder configBuilder;

    @Override
    public int getArgumentCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "Include generated date";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return asList("-includeGeneratedDate");
    }

    @Override
    public String getParameters() {
        return "";
    }

    @Override
    public boolean process(String option,
            List<String> arguments) {
        configBuilder.includeGenerationDate(true);
        return true;
    }

}
