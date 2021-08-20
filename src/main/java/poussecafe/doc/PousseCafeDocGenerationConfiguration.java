package poussecafe.doc;

import java.util.Optional;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Builder(builderClassName = "Builder")
@Accessors(fluent = true)
public class PousseCafeDocGenerationConfiguration {

    @NonNull
    String domainName;

    @NonNull
    String version;

    @NonNull
    String outputDirectory;

    boolean includeGenerationDate;

    @Default
    Optional<String> customDotExecutable = Optional.empty();

    @Default
    Optional<String> customFdpExecutable = Optional.empty();

    @NonNull
    String pdfFileName;

    private boolean debug;
}
