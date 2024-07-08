package poussecafe.doc;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.Module;

public class GraphImagesWriter {

    private static final String IMAGES_SUB_DIRECTORY = "img";

    public void writeImages(Domain domain) {
        try {
            File outputDirectory = outputDirectory();
            for (Module moduleDoc : domain.modules()) {
                logger.debug("Drawing BC " + moduleDoc.documentation().name() + " graph...");
                graphImageWriter
                        .writeImage(GraphFactory.buildModuleGraph(moduleDoc, domain), outputDirectory,
                                moduleDoc.documentation().id());

                writeAggregatesGraphs(moduleDoc, domain);
                writeDomainProcessesGraphs(moduleDoc, domain);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing graphs", e);
        }
    }

    private Logger logger = LoggerFactory.getLogger(GraphImagesWriter.class);

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
            logger.debug("Drawing aggregate " + aggregateDoc.name() + " graph...");

            String aggregateGraphBaseName = module.documentation().id() + "_" + aggregateDoc.id();
            graphImageWriter.writeImage(GraphFactory.buildAggregateGraph(aggregate, domain), outputDirectory,
                    aggregateGraphBaseName);

            String aggregateGraphEventsBaseName = module.documentation().id() + "_" + aggregateDoc.id() + "_events";
            graphImageWriter.writeImage(AggregateEventsGraphFactory.buildGraph(aggregate, domain), outputDirectory,
                    aggregateGraphEventsBaseName);
        }
    }

    private void writeDomainProcessesGraphs(Module module, Domain domain) throws IOException {
        File outputDirectory = outputDirectory();
        for (DocumentationItem domainProcessDoc : module.processes()) {
            logger.debug("Drawing domain process " + domainProcessDoc.name() + " graph...");
            graphImageWriter.writeImage(
                    GraphFactory.buildDomainProcessGraph(domainProcessDoc, domain),
                    outputDirectory,
                    module.documentation().id() + "_" + domainProcessDoc.id());
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public GraphImagesWriter build() {
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
}
