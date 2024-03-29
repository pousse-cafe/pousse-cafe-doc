package poussecafe.doc.doclet.options;

import java.util.List;
import java.util.Objects;
import jdk.javadoc.doclet.Doclet.Option;
import poussecafe.doc.doclet.PousseCafeDocletConfiguration;

import static java.util.Arrays.asList;

public class BasePackageOption implements Option {

    public BasePackageOption(PousseCafeDocletConfiguration.BaseBuilder configBuilder) {
        Objects.requireNonNull(configBuilder);
        this.configBuilder = configBuilder;
    }

    private PousseCafeDocletConfiguration.BaseBuilder configBuilder;

    @Override
    public int getArgumentCount() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Domain base package";
    }

    @Override
    public Kind getKind() {
        return Kind.STANDARD;
    }

    @Override
    public List<String> getNames() {
        return asList("-basePackage");
    }

    @Override
    public String getParameters() {
        return "<package>";
    }

    @Override
    public boolean process(String option,
            List<String> arguments) {
        configBuilder.basePackage(arguments.get(0));
        return true;
    }

}
