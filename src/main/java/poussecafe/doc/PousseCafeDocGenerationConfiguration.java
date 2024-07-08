package poussecafe.doc;

import java.util.Objects;
import java.util.Optional;

public class PousseCafeDocGenerationConfiguration {

    public String domainName() {
        return domainName;
    }

    private String domainName;

    public String version() {
        return version;
    }

    private String version;
    
    public String outputDirectory() {
        return outputDirectory;
    }

    private String outputDirectory;

    public boolean includeGenerationDate() {
        return includeGenerationDate;
    }

    private boolean includeGenerationDate;

    public Optional<String> customDotExecutable() {
        return customDotExecutable;
    }

    private Optional<String> customDotExecutable = Optional.empty();

    public Optional<String> customFdpExecutable() {
        return customFdpExecutable;
    }

    private Optional<String> customFdpExecutable = Optional.empty();

    public String pdfFileName() {
        return pdfFileName;
    }

    private String pdfFileName;

    public boolean debug() {
        return debug;
    }

    private boolean debug;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public PousseCafeDocGenerationConfiguration build() {
            return configuration;
        }

        private PousseCafeDocGenerationConfiguration configuration = new PousseCafeDocGenerationConfiguration();

        public Builder domainName(String domainName) {
            Objects.requireNonNull(domainName);
            configuration.domainName = domainName;
            return this;
        }

        public Builder version(String version) {
            Objects.requireNonNull(version);
            configuration.version = version;
            return this;
        }
        
        public Builder outputDirectory(String outputDirectory) {
            Objects.requireNonNull(outputDirectory);
            configuration.outputDirectory = outputDirectory;
            return this;
        }

        public Builder includeGenerationDate(boolean includeGenerationDate) {
            configuration.includeGenerationDate = includeGenerationDate;
            return this;
        }

        public Builder customDotExecutable(Optional<String> customDotExecutable) {
            Objects.requireNonNull(customDotExecutable);
            configuration.customDotExecutable = customDotExecutable;
            return this;
        }

        public Builder customFdpExecutable(Optional<String> customFdpExecutable) {
            Objects.requireNonNull(customFdpExecutable);
            configuration.customFdpExecutable = customFdpExecutable;
            return this;
        }

        public Builder pdfFileName(String pdfFileName) {
            Objects.requireNonNull(pdfFileName);
            configuration.pdfFileName = pdfFileName;
            return this;
        }

        public Builder debug(boolean debug) {
            configuration.debug = debug;
            return this;
        }
    }
}
