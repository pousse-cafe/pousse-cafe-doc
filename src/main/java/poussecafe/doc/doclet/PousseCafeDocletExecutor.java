package poussecafe.doc.doclet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import static java.util.stream.Collectors.joining;
import static poussecafe.collection.Collections.asSet;

public class PousseCafeDocletExecutor {

    public PousseCafeDocletExecutor(PousseCafeDocletConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    private PousseCafeDocletConfiguration configuration;

    public void execute() {
        List<String> javadocArgs = new ArrayList<>();
        addStandardOptionsTo(javadocArgs);
        addDocletOptionsTo(javadocArgs);

        var documentationTool = ToolProvider.getSystemDocumentationTool();
        JavaFileManager fileManager = documentationTool.getStandardFileManager(null, null, null);
        try {
            Iterable<JavaFileObject> compilationUnits = fileManager.list(StandardLocation.SOURCE_PATH, configuration.basePackage(), asSet(Kind.SOURCE), true);
            DocumentationTask task = documentationTool.getTask(configuration.errorWriter(), fileManager, null, PousseCafeDoclet.class, javadocArgs, compilationUnits);
            task.call();
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to generate documentation", e);
        }
    }

    private void addStandardOptionsTo(List<String> javadocArgs) {
        String pathElementSeparator = SystemDependentInformation.pathElementSeparator();
        javadocArgs.add("-sourcepath"); javadocArgs.add(configuration.sourcePath().stream().collect(joining(pathElementSeparator)));
        javadocArgs.add("-subpackages"); javadocArgs.add(configuration.basePackage());
        if(!configuration.classPath().isEmpty()) {
            javadocArgs.add("-classpath"); javadocArgs.add(configuration.classPath().stream().collect(joining(pathElementSeparator)));
        }
    }

    private void addDocletOptionsTo(List<String> javadocArgs) {
        var generationConfiguration = configuration.generationConfiguration();
        javadocArgs.add("-output"); javadocArgs.add(generationConfiguration.outputDirectory());
        javadocArgs.add("-pdfFile"); javadocArgs.add(generationConfiguration.pdfFileName());
        javadocArgs.add("-domain"); javadocArgs.add(generationConfiguration.domainName());
        javadocArgs.add("-version"); javadocArgs.add(generationConfiguration.version());
        javadocArgs.add("-basePackage"); javadocArgs.add(configuration.basePackage());
        if(generationConfiguration.includeGenerationDate()) {
            javadocArgs.add("-includeGeneratedDate");
        }
        var customDotExecutable = generationConfiguration.customDotExecutable();
        if(customDotExecutable.isPresent()) {
            javadocArgs.add("-customDotExecutable"); javadocArgs.add(customDotExecutable.orElseThrow());
        }
        var customFdpExecutable = generationConfiguration.customFdpExecutable();
        if(customFdpExecutable.isPresent()) {
            javadocArgs.add("-customFdpExecutable"); javadocArgs.add(customFdpExecutable.orElseThrow());
        }
    }
}
