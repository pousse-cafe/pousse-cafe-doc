package poussecafe.doc.doclet;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import poussecafe.doc.PousseCafeDocGenerationConfiguration;

import static java.util.Collections.emptyList;

public class PousseCafeDocletConfiguration {

    public PousseCafeDocGenerationConfiguration generationConfiguration() {
        return generationConfiguration;
    }

    private PousseCafeDocGenerationConfiguration generationConfiguration;

    public String basePackage() {
        return basePackage;
    }

    private String basePackage;

    public List<String> sourcePath() {
        return Collections.unmodifiableList(sourcePath);
    }

    private List<String> sourcePath;

    public PrintWriter errorWriter() {
        return errorWriter;
    }

    private PrintWriter errorWriter;

    public PrintWriter warningWriter() {
        return warningWriter;
    }

    private PrintWriter warningWriter;
    
    public PrintWriter noticeWriter() {
        return noticeWriter;
    }

    private PrintWriter noticeWriter;

    public List<String> classPath() {
        return Collections.unmodifiableList(classPath);
    }

    private List<String> classPath = emptyList();

    public static BaseBuilder builder() {
        return new BaseBuilder();
    }

    public static class BaseBuilder {

        private PousseCafeDocletConfiguration configuration = new PousseCafeDocletConfiguration();

        public PousseCafeDocletConfiguration build() {
            Objects.requireNonNull(configuration.generationConfiguration);
            Objects.requireNonNull(configuration.basePackage);
            if(configuration.sourcePath == null || configuration.sourcePath.isEmpty()) {
                throw new IllegalStateException("Source path must contain at least one element");
            }
            if(configuration.errorWriter == null) {
                configuration.errorWriter = new PrintWriter(System.err);
                configuration.warningWriter = new PrintWriter(System.err);
                configuration.noticeWriter = new PrintWriter(System.out);
            }
            return configuration;
        }

        public BaseBuilder generationConfiguration(PousseCafeDocGenerationConfiguration generationConfiguration) {
            configuration.generationConfiguration = generationConfiguration;
            return this;
        }

        public BaseBuilder basePackage(String basePackage) {
            configuration.basePackage = basePackage;
            return this;
        }

        public BaseBuilder sourcePath(List<String> sourcePath) {
            configuration.sourcePath = sourcePath;
            return this;
        }

        public BaseBuilder errorWriter(PrintWriter errorWriter) {
            configuration.errorWriter = errorWriter;
            return this;
        }

        public BaseBuilder warningWriter(PrintWriter warningWriter) {
            configuration.warningWriter = warningWriter;
            return this;
        }

        public BaseBuilder noticeWriter(PrintWriter noticeWriter) {
            configuration.noticeWriter = noticeWriter;
            return this;
        }

        public BaseBuilder classPath(List<String> classPath) {
            configuration.classPath = classPath;
            return this;
        }
    }
}
