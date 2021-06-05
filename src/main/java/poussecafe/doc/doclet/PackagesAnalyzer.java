package poussecafe.doc.doclet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.ElementFilter;
import jdk.javadoc.doclet.DocletEnvironment;
import poussecafe.doc.annotations.AnnotationUtils;
import poussecafe.doc.model.AnnotationsResolver;
import poussecafe.source.Ignore;

public class PackagesAnalyzer {

    public static class Builder {

        private PackagesAnalyzer analyzer = new PackagesAnalyzer();

        public Builder packageDocConsumer(Consumer<PackageElement> packageElementConsumer) {
            Objects.requireNonNull(packageElementConsumer);
            analyzer.packageDocConsumers.add(packageElementConsumer);
            return this;
        }

        public PackagesAnalyzer build() {
            return analyzer;
        }
    }

    private PackagesAnalyzer() {

    }

    private DocletEnvironment docletEnvironment;

    private List<Consumer<PackageElement>> packageDocConsumers = new ArrayList<>();

    public void analyzeCode() {
        Set<PackageElement> classes = ElementFilter.packagesIn(docletEnvironment.getIncludedElements());
        for (PackageElement classDoc : classes) {
            if (!annotationsResolver.isIgnored(classDoc)
                    && AnnotationUtils.annotation(classDoc, Ignore.class).isEmpty()) {
                processPackageDoc(classDoc);
            }
        }
    }

    private AnnotationsResolver annotationsResolver;

    private void processPackageDoc(PackageElement classDoc) {
        for(Consumer<PackageElement> consumer : packageDocConsumers) {
            consumer.accept(classDoc);
        }
    }
}
