package poussecafe.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.Graph;

public class GraphImageWriter {

    public void writeImage(Graph graph, File outputDirectory,
            String graphName) throws IOException {
        var dotFile = new File(outputDirectory, graphName + ".dot");
        var printStream = new PrintStream(dotFile);
        var dotPrinter = new DotPrinter(printStream);
        dotPrinter.print(graph);
        printStream.close();

        String executable = chooseExecutable(graph);
        var process = new ProcessBuilder(executable, "-Tpng",
                dotFile.getAbsolutePath()).start();

        var pngFile = new File(outputDirectory, graphName + ".png");
        try(var pngOutputStream = new FileOutputStream(pngFile)) {
            IOUtils.copy(process.getInputStream(), pngOutputStream);
        }
    }

    private String chooseExecutable(Graph graph) {
        if(graph instanceof DirectedGraph) {
            return customDotExecutable.orElse(DEFAULT_DOT_EXECUTABLE);
        } else {
            return customFdpExecutable.orElse(DEFAULT_FDP_EXECUTABLE);
        }
    }

    private Optional<String> customDotExecutable = Optional.empty();

    private static final String DEFAULT_DOT_EXECUTABLE = "dot";

    private Optional<String> customFdpExecutable = Optional.empty();

    private static final String DEFAULT_FDP_EXECUTABLE = "fdp";

    public static class Builder {

        private GraphImageWriter writer = new GraphImageWriter();

        public Builder customDotExecutable(Optional<String> customDotExecutable) {
            writer.customDotExecutable = customDotExecutable;
            return this;
        }

        public Builder customFdpExecutable(Optional<String> customFdpExecutable) {
            writer.customFdpExecutable = customFdpExecutable;
            return this;
        }

        public GraphImageWriter build() {
            Objects.requireNonNull(writer.customDotExecutable);
            Objects.requireNonNull(writer.customFdpExecutable);
            return writer;
        }
    }

    private GraphImageWriter() {

    }
}
