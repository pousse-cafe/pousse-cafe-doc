package poussecafe.doc;

import java.io.File;
import java.io.IOException;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.GraphFactory;
import poussecafe.doc.model.Module;

public class GraphImagesWriter {

    private static final String IMAGES_SUB_DIRECTORY = "img";

    public GraphImagesWriter(PousseCafeDocletConfiguration configuration) {
        this.configuration = configuration;
        graphImageWriter = new GraphImageWriter.Builder()
                .customDotExecutable(configuration.customDotExecutable())
                .customFdpExecutable(configuration.customFdpExecutable())
                .build();
    }

    private PousseCafeDocletConfiguration configuration;

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
        File outputDirectory = new File(configuration.outputDirectory(), IMAGES_SUB_DIRECTORY);
        outputDirectory.mkdirs();
        return outputDirectory;
    }

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
}
