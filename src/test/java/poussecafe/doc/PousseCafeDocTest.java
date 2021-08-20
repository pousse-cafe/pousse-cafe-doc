package poussecafe.doc;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import poussecafe.doc.doclet.PousseCafeDocletConfiguration;
import poussecafe.doc.doclet.PousseCafeDocletExecutor;
import poussecafe.files.Difference;
import poussecafe.files.DifferenceType;
import poussecafe.source.analysis.SourceModelBuilder;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public class PousseCafeDocTest {

    @Test
    public void generatesExpectedDoc() throws IOException {
        givenBasePackage("poussecafe.sample.test");
        givenGeneratorConfiguration();
        givenEmptyOutputDirectory();
        whenExecutingGenerator();
        thenGeneratedDocContainsExpectedData("expected-doc-test");
    }

    private void givenBasePackage(String value) {
        basePackage = value;
    }

    private String basePackage;

    private void givenGeneratorConfiguration() {
        generationConfiguration = PousseCafeDocGenerationConfiguration.builder()
                .domainName("Pousse-Café Doc")
                .outputDirectory(System.getProperty("java.io.tmpdir") + "/" + basePackage)
                .pdfFileName("domain.pdf")
                .version("Test")
                .includeGenerationDate(false)
                .customDotExecutable(Optional.of("dot"))
                .customFdpExecutable(Optional.of("fdp"))
                .build();
    }

    private PousseCafeDocGenerationConfiguration generationConfiguration;

    private void whenExecutingGenerator() throws IOException {
        var builder = new SourceModelBuilder();
        builder.includeTree(Path.of(System.getProperty("user.dir") + "/src/test/java/", basePackage.replace('.', '/')));
        var generator = PousseCafeDocGenerator.builder()
            .configuration(generationConfiguration)
            .model(builder.build())
            .build();
        generator.generate();
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void docletGeneratesExpectedDoc() {
        givenBasePackage("poussecafe.sample.test");
        givenDocletConfiguration();
        givenEmptyOutputDirectory();
        whenExecutingDoclet();
        thenGeneratedDocContainsExpectedData("expected-doc-test");
    }

    private void givenDocletConfiguration() {
        givenGeneratorConfiguration();
        configuration = PousseCafeDocletConfiguration.builder()
                .generationConfiguration(generationConfiguration)
                .basePackage(basePackage)
                .sourcePath(asList(System.getProperty("user.dir") + "/src/test/java/"))
                .build();
        assumeTrue(executableInstalled("dot", configuration.generationConfiguration().customDotExecutable().orElseThrow()));
        assumeTrue(executableInstalled("fdp", configuration.generationConfiguration().customFdpExecutable().orElseThrow()));
    }

    private PousseCafeDocletConfiguration configuration;

    private void givenEmptyOutputDirectory() {
        File outputDirectory = new File(generationConfiguration.outputDirectory());
        new File(outputDirectory, "index.html").delete();
    }

    private void whenExecutingDoclet() {
        new PousseCafeDocletExecutor(configuration).execute();
    }

    private void thenGeneratedDocContainsExpectedData(String expectedDocFolder) {
        Path expectedDocDirectory = Paths.get(System.getProperty("user.dir"), "src", "test", "resources", expectedDocFolder);
        Path targetDirectory = Paths.get(generationConfiguration.outputDirectory());
        try {
            assertDifferences(poussecafe.files.Tree.compareTrees(targetDirectory, expectedDocDirectory, ".dot"));
        } catch (IOException e) {
            fail();
        }
    }

    private void assertDifferences(List<Difference> differences) {
        for(Difference difference : differences) {
            if(difference.type() == DifferenceType.TARGET_DOES_NOT_EXIST) {
                assertTrue("File " + difference.relativePath() + " does not exist", false);
            } else if(difference.type() == DifferenceType.CONTENT_DOES_NOT_MATCH) {
                String message = message(difference);
                assertTrue(message, false);
            }
        }
    }

    private String message(Difference difference) {
        StringBuilder message = new StringBuilder();
        message.append("File ");
        message.append(difference.relativePath());
        message.append(" does not match expected content");
        if(!difference.contentSorted()) {
            Patch<String> diff = DiffUtils.diffInline(difference.expectedContent(), difference.targetContent());
            message.append(": ");
            message.append(diff.toString());
        }
        return message.toString();
    }

    private boolean executableInstalled(String type, String executable) {
        try {
            Process process = new ProcessBuilder(executable, "-V").start();
            String version = IOUtils.toString(process.getErrorStream(), Charset.defaultCharset());
            process.waitFor();
            logger.info("Detected custom {} executable. Version: {}", type, version);
            return process.exitValue() == 0;
        } catch (Exception e) {
            logger.warn(String.format("Failed to check custom %s executable: %s", type, executable), e);
            logger.info("Consider installing graphviz package to enable this test.");
            return false;
        }
    }

    @Test
    public void docletGeneratesExpectedDocUsingDeprecated() {
        givenBasePackage("poussecafe.sample.test_deprecated");
        givenDocletConfiguration();
        givenEmptyOutputDirectory();
        whenExecutingDoclet();
        thenGeneratedDocContainsExpectedData("expected-doc-test-deprecated");
    }
}
