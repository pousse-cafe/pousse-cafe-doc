package poussecafe.doc;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import poussecafe.doc.doclet.Logger;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.GraphFactory;
import poussecafe.doc.model.Module;

import static java.util.Objects.requireNonNull;

public class GraphImagesWriter {

    private static final String IMAGES_SUB_DIRECTORY = "img";

    public void writeImages(Domain domain) {
        try {
            File outputDirectory = outputDirectory();
            for (Module moduleDoc : domain.modules()) {
                Logger.debug("Drawing BC " + moduleDoc.documentation().name() + " graph...");
                graphImageWriter
                        .writeImage(graphFactory.buildModuleGraph(moduleDoc, domain), outputDirectory,
                                moduleDoc.documentation().id());

                writeAggregatesGraphs(moduleDoc, domain);
                writeDomainProcessesGraphs(moduleDoc, domain);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing graphs", e);
        }
    }

    private File outputDirectory() {
        var outputDirectory = new File(outputDirectoryPath, IMAGES_SUB_DIRECTORY);
        outputDirectory.mkdirs();
        return outputDirectory;
    }

    private String outputDirectoryPath;

    private GraphImageWriter graphImageWriter;

    private void writeAggregatesGraphs(Module module, Domain domain) throws IOException {
        File outputDirectory = outputDirectory();
        for (Aggregate aggregate : module.aggregates()) {
            var aggregateDoc = aggregate.documentation();
            Logger.debug("Drawing aggregate " + aggregateDoc.name() + " graph...");

            String aggregateGraphBaseName = module.documentation().id() + "_" + aggregateDoc.id();
            graphImageWriter.writeImage(graphFactory.buildAggregateGraph(aggregate, domain), outputDirectory,
                    aggregateGraphBaseName);

            String aggregateGraphEventsBaseName = module.documentation().id() + "_" + aggregateDoc.id() + "_events";
            graphImageWriter.writeImage(aggregateEventsGraphFactory.buildGraph(aggregate, domain), outputDirectory,
                    aggregateGraphEventsBaseName);
        }
    }

    private GraphFactory graphFactory;

    private AggregateEventsGraphFactory aggregateEventsGraphFactory;

    private void writeDomainProcessesGraphs(Module module, Domain domain) throws IOException {
        File outputDirectory = outputDirectory();
        for (DocumentationItem domainProcessDoc : module.processes()) {
            Logger.debug("Drawing domain process " + domainProcessDoc.name() + " graph...");
            graphImageWriter.writeImage(
                    graphFactory.buildDomainProcessGraph(domainProcessDoc, domain),
                    outputDirectory,
                    module.documentation().id() + "_" + domainProcessDoc.id());
        }
    }

    public static class Builder {

        public GraphImagesWriter build() {
            requireNonNull(writer.outputDirectoryPath);

            writer.graphImageWriter = new GraphImageWriter.Builder()
                    .customDotExecutable(customDotExecutable)
                    .customFdpExecutable(customFdpExecutable)
                    .build();

            return writer;
        }

        private GraphImagesWriter writer = new GraphImagesWriter();

        public Builder outputDirectoryPath(String outputDirectoryPath) {
            writer.outputDirectoryPath = outputDirectoryPath;
            return this;
        }

        public Builder customDotExecutable(Optional<String> customDotExecutable) {
            this.customDotExecutable = customDotExecutable;
            return this;
        }

        private Optional<String> customDotExecutable = Optional.empty();

        public Builder customFdpExecutable(Optional<String> customFdpExecutable) {
            this.customFdpExecutable = customFdpExecutable;
            return this;
        }

        private Optional<String> customFdpExecutable = Optional.empty();
    }

    private GraphImagesWriter() {

    }
}
