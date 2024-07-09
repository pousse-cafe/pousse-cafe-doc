package poussecafe.doc.doclet;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import poussecafe.doc.GraphImagesWriter;
import poussecafe.doc.HtmlWriter;
import poussecafe.doc.PdfWriter;
import poussecafe.doc.PousseCafeDocBundle;
import poussecafe.doc.PousseCafeDocGenerationConfiguration;
import poussecafe.doc.doclet.options.BasePackageOption;
import poussecafe.doc.doclet.options.CustomDotExecutableOption;
import poussecafe.doc.doclet.options.CustomFdpExecutableOption;
import poussecafe.doc.doclet.options.DomainOption;
import poussecafe.doc.doclet.options.IncludeGeneratedDateOption;
import poussecafe.doc.doclet.options.OutputPathOption;
import poussecafe.doc.doclet.options.PdfFileNameOption;
import poussecafe.doc.doclet.options.SourcePathOption;
import poussecafe.doc.doclet.options.VersionOption;
import poussecafe.doc.model.ClassDocRepository;
import poussecafe.doc.model.DocletAccess;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.DomainFactory;
import poussecafe.exception.PousseCafeException;
import poussecafe.messaging.sync.SynchronousMessaging;
import poussecafe.runtime.ProcessingMode;
import poussecafe.runtime.Runtime;
import poussecafe.storage.internal.InternalStorage;

public class PousseCafeDoclet implements Doclet {

    public PousseCafeDoclet() {
        docletConfigBuilder = PousseCafeDocletConfiguration.builder();
        generatorConfigBuilder = PousseCafeDocGenerationConfiguration.builder();
    }

    private PousseCafeDocletConfiguration.BaseBuilder docletConfigBuilder;

    private PousseCafeDocGenerationConfiguration.Builder generatorConfigBuilder;

    private Runtime runtime;

    @Override
    public void init(Locale locale,
            Reporter reporter) {
        Logger.setRootDoc(reporter);
    }

    @Override
    public String getName() {
        return "DDD Documentation";
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        Set<Option> supportedOptions = new HashSet<>();
        supportedOptions.add(new DomainOption(generatorConfigBuilder));
        supportedOptions.add(new IncludeGeneratedDateOption(generatorConfigBuilder));
        supportedOptions.add(new OutputPathOption(generatorConfigBuilder));
        supportedOptions.add(new VersionOption(generatorConfigBuilder));
        supportedOptions.add(new CustomDotExecutableOption(generatorConfigBuilder));
        supportedOptions.add(new CustomFdpExecutableOption(generatorConfigBuilder));
        supportedOptions.add(new PdfFileNameOption(generatorConfigBuilder));

        supportedOptions.add(new BasePackageOption(docletConfigBuilder));
        supportedOptions.add(new SourcePathOption(docletConfigBuilder));

        return supportedOptions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_9;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        this.environment = environment;
        docletConfigBuilder.generationConfiguration(generatorConfigBuilder.build());
        configuration = docletConfigBuilder.build();

        runtime = new Runtime.Builder()
                .withBundle(PousseCafeDocBundle.configure()
                        .defineThenImplement()
                        .messaging(SynchronousMessaging.instance())
                        .storage(InternalStorage.instance())
                        .build())
                .processingMode(ProcessingMode.synchronous())
                .withInjectableService(DocletEnvironment.class, environment)
                .withInjectableService(configuration)
                .build();

        Logger.info("Starting Pousse-Café doclet...");
        try {
            runtime.start();

            registerClassDocs();
            analyzeCode();
            createOutputFolder();

            var domain = runtime.environment().service(DomainFactory.class).orElseThrow().buildDomain();
            writeGraphs(domain);
            writeHtml(domain);
            writePdf();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private DocletEnvironment environment;

    private PousseCafeDocletConfiguration configuration;

    private void registerClassDocs() {
        Set<TypeElement> typeElements = runtime.environment().service(DocletAccess.class).orElseThrow(NoSuchElementException::new).typeElements();
        runtime.environment()
                .service(ClassDocRepository.class)
                .orElseThrow(PousseCafeException::new)
                .registerTypeElements(typeElements);
    }

    private void analyzeCode() {
        detectModules();
        detectModulesComponents();
        detectDomainProcesses();
        detectRelations();
    }

    private void detectModules() {
        detectClassBasedModules();
        detectPackageInfoModules();
    }

    private void detectClassBasedModules() {
        var moduleDocCreator = new ClassModuleDocCreator();
        runtime.injector().injectDependenciesInto(moduleDocCreator);

        ClassesAnalyzer classCodeAnalyzer = new ClassesAnalyzer.Builder()
                .classDocConsumer(moduleDocCreator)
                .build();
        runtime.injector().injectDependenciesInto(classCodeAnalyzer);
        classCodeAnalyzer.analyzeCode();
    }

    private void detectPackageInfoModules() {
        var packageInfoModuleDocCreator = new PackageInfoModuleDocCreator();
        runtime.injector().injectDependenciesInto(packageInfoModuleDocCreator);

        PackagesAnalyzer codeAnalyzer = new PackagesAnalyzer.Builder()
                .packageDocConsumer(packageInfoModuleDocCreator)
                .build();
        runtime.injector().injectDependenciesInto(codeAnalyzer);
        codeAnalyzer.analyzeCode();
    }

    private void detectModulesComponents() {
        var aggregateDocCreator = new AggregateDocCreator(environment);
        runtime.injector().injectDependenciesInto(aggregateDocCreator);

        var serviceDocCreator = new ServiceDocCreator(environment);
        runtime.injector().injectDependenciesInto(serviceDocCreator);

        var entityDocCreator = new EntityDocCreator(environment);
        runtime.injector().injectDependenciesInto(entityDocCreator);

        var valueObjectDocCreator = new ValueObjectDocCreator(environment);
        runtime.injector().injectDependenciesInto(valueObjectDocCreator);

        var messageListenerDocCreator = new ProcessStepDocCreator(environment);
        runtime.injector().injectDependenciesInto(messageListenerDocCreator);

        ClassesAnalyzer codeAnalyzer = new ClassesAnalyzer.Builder()
                .classDocConsumer(aggregateDocCreator)
                .classDocConsumer(serviceDocCreator)
                .classDocConsumer(entityDocCreator)
                .classDocConsumer(valueObjectDocCreator)
                .classDocConsumer(messageListenerDocCreator)
                .build();
        runtime.injector().injectDependenciesInto(codeAnalyzer);
        codeAnalyzer.analyzeCode();
    }

    private void detectDomainProcesses() {
        var domainProcessDocCreator = new DomainProcessDocCreator(environment);
        runtime.injector().injectDependenciesInto(domainProcessDocCreator);

        ClassesAnalyzer codeAnalyzer = new ClassesAnalyzer.Builder()
                .classDocConsumer(domainProcessDocCreator)
                .build();
        runtime.injector().injectDependenciesInto(codeAnalyzer);
        codeAnalyzer.analyzeCode();
    }

    private void detectRelations() {
        var relationCreator = new RelationCreator();
        runtime.injector().injectDependenciesInto(relationCreator);

        ClassesAnalyzer codeAnalyzer = new ClassesAnalyzer.Builder()
                .classDocConsumer(relationCreator)
                .build();
        runtime.injector().injectDependenciesInto(codeAnalyzer);
        codeAnalyzer.analyzeCode();
    }

    private void createOutputFolder() {
        var outputDirectory = new File(configuration.generationConfiguration().outputDirectory());
        outputDirectory.mkdirs();
    }

    private void writeGraphs(Domain domain) {
        GraphImagesWriter graphsWriter = GraphImagesWriter.builder()
                .customDotExecutable(configuration.generationConfiguration().customDotExecutable())
                .customFdpExecutable(configuration.generationConfiguration().customFdpExecutable())
                .outputDirectoryPath(configuration.generationConfiguration().outputDirectory())
                .build();
        graphsWriter.writeImages(domain);
    }

    private void writeHtml(Domain domain) {
        var htmlWriter = new HtmlWriter.Builder()
                .includeGenerationDate(configuration.generationConfiguration().includeGenerationDate())
                .outputDirectoryPath(configuration.generationConfiguration().outputDirectory())
                .build();
        htmlWriter.writeHtml(domain);
    }

    private void writePdf() {
        var pdfWriter = new PdfWriter(configuration.generationConfiguration());
        pdfWriter.writePdf();
    }
}
