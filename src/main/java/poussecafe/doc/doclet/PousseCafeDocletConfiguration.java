package poussecafe.doc.doclet;

import java.io.PrintWriter;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import poussecafe.doc.PousseCafeDocGenerationConfiguration;

import static java.util.Collections.emptyList;

@Value
@Builder(builderClassName = "BaseBuilder")
@Accessors(fluent = true)
public class PousseCafeDocletConfiguration {

    @NonNull
    private PousseCafeDocGenerationConfiguration generationConfiguration;

    @NonNull
    private String basePackage;

    private List<String> sourcePath;

    private PrintWriter errorWriter;

    private PrintWriter warningWriter;

    private PrintWriter noticeWriter;

    @Default
    private List<String> classPath = emptyList();

    public static CustomBuilder builder() {
        return new CustomBuilder();
    }

    public static class CustomBuilder extends BaseBuilder {

        @Override
        public PousseCafeDocletConfiguration build() {
            if(super.sourcePath == null || super.sourcePath.isEmpty()) {
                throw new IllegalStateException("Source path must contain at least one element");
            }
            if(super.errorWriter == null) {
                super.errorWriter = new PrintWriter(System.err);
                super.warningWriter = new PrintWriter(System.err);
                super.noticeWriter = new PrintWriter(System.out);
            }
            return super.build();
        }
    }
}
