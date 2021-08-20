package poussecafe.doc;

import java.io.File;
import java.io.FileOutputStream;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;
import poussecafe.doc.doclet.Logger;

import static java.util.Objects.requireNonNull;

public class PdfWriter {

    public void writePdf() {
        try {
            if (configuration.debug()) {
                System.getProperties().setProperty("xr.util-logging.loggingEnabled", "true");
                XRLog.setLoggingEnabled(true);
            }

            Logger.debug("Writing PDF...");
            var pdfRenderer = new ITextRenderer();
            pdfRenderer.setDocument(new File(configuration.outputDirectory(), "index.html"));
            pdfRenderer.layout();
            var pdfFile = new File(configuration.outputDirectory(), configuration.pdfFileName());
            var pdfOutputStream = new FileOutputStream(pdfFile);
            pdfRenderer.createPDF(pdfOutputStream);
            pdfRenderer.finishPDF();
            pdfOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error while writing PDF", e);
        }
    }

    public PdfWriter(PousseCafeDocGenerationConfiguration configuration) {
        requireNonNull(configuration);
        this.configuration = configuration;
    }

    private PousseCafeDocGenerationConfiguration configuration;
}
