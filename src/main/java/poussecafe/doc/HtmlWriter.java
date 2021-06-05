package poussecafe.doc;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;
import poussecafe.doc.doclet.PousseCafeDocletConfiguration;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.DomainProcessSteps;
import poussecafe.doc.model.DomainProcessStepsFactory;
import poussecafe.doc.model.Module;
import poussecafe.doc.model.UbiquitousLanguageEntry;
import poussecafe.doc.model.UbiquitousLanguageFactory;
import poussecafe.doc.model.domainprocessdoc.Step;

import static java.util.stream.Collectors.toList;

public class HtmlWriter {

    public void writeHtml(Domain domain) {
        try(FileWriter stream = new FileWriter(new File(configuration.outputDirectory(), "index.html"))) {
            copyCss();

            Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_28);
            freemarkerConfig.setClassForTemplateLoading(getClass(), "/");
            Template template = freemarkerConfig.getTemplate("index.html");

            HashMap<String, Object> domainMap = new HashMap<>();
            domainMap.put("name", domain.name());
            domainMap.put("version", domain.version());

            domainMap.put("modules",
                            domain.modules()
                                    .stream()
                                    .filter(module -> !module.isEmpty())
                                    .sorted(this::compareModules)
                                    .map(item -> adapt(item, domain))
                                    .collect(toList()));

            HashMap<String, Object> model = new HashMap<>();
            model.put("includeGenerationDate", configuration.includeGenerationDate());
            model.put("domain", domainMap);
            model.put("generationDate", new Date());
            model.put("ubiquitousLanguage",
                            ubitquitousLanguageFactory
                                    .buildUbiquitousLanguage(domain)
                                    .stream()
                                    .filter(doc -> !doc.componentDoc().description().trivial())
                                    .map(this::adapt)
                                    .collect(toList()));
            template.process(model, stream);
        } catch (Exception e) {
            throw new RuntimeException("Error while writing HTML", e);
        }
    }

    private PousseCafeDocletConfiguration configuration;

    private int compareModules(Module moduleDoc1, Module moduleDoc2) {
        DocumentationItem doc1 = moduleDoc1.documentation();
        DocumentationItem doc2 = moduleDoc2.documentation();
        return compareTo(doc1, doc2);
    }

    private int compareTo(DocumentationItem componentDoc1,
            DocumentationItem componentDoc2) {
        return componentDoc1.name().compareTo(componentDoc2.name());
    }

    private HashMap<String, Object> adapt(Module module, Domain domain) {
        HashMap<String, Object> view = new HashMap<>();
        DocumentationItem moduleDoc = module.documentation();
        view.put("id", moduleDoc.id());
        view.put("name", moduleDoc.name());
        view.put("description", moduleDoc.description().description().orElse(""));

        view.put("aggregates", module.aggregates()
                .stream()
                .filter(aggregate -> !aggregate.documentation().description().trivial())
                .sorted(this::compareAggregates)
                .map(this::adapt)
                .collect(toList()));

        view.put("services", module.services()
                .stream()
                .filter(doc -> !doc.description().trivial())
                .sorted(this::compareTo)
                .map(this::adapt)
                .collect(toList()));

        view.put("domainProcesses", module.processes()
                .stream()
                .filter(doc -> !doc.description().trivial())
                .sorted(this::compareTo)
                .map(item -> adaptDomainProcess(item, domain))
                .collect(toList()));

        return view;
    }

    private int compareAggregates(Aggregate aggregateDoc1, Aggregate aggregateDoc2) {
        var doc1 = aggregateDoc1.documentation();
        var doc2 = aggregateDoc2.documentation();
        return compareTo(doc1, doc2);
    }

    private HashMap<String, Object> adapt(Aggregate aggregate) {
        HashMap<String, Object> view = new HashMap<>();
        var aggregateDoc = aggregate.documentation();
        view.put("id", aggregateDoc.id());
        view.put("name", aggregateDoc.name());
        view.put("description", aggregateDoc.description().description().orElse(""));

        view.put("entities", aggregate.entities().stream()
                .filter(doc -> !doc.description().trivial())
                .sorted(this::compareTo)
                .map(this::adapt)
                .collect(toList()));

        view.put("valueObjects", aggregate.valueObjects().stream()
                .filter(doc -> !doc.description().trivial())
                .sorted(this::compareTo)
                .map(this::adapt)
                .collect(toList()));

        return view;
    }

    private HashMap<String, Object> adapt(DocumentationItem serviceDoc) {
        HashMap<String, Object> view = new HashMap<>();
        view.put("id", serviceDoc.id());
        view.put("name", serviceDoc.name());
        view.put("description", serviceDoc.description().description().orElse(""));
        return view;
    }

    private HashMap<String, Object> adaptDomainProcess(DocumentationItem domainProcessDoc, Domain domain) {
        HashMap<String, Object> view = new HashMap<>();
        view.put("id", domainProcessDoc.id());
        view.put("name", domainProcessDoc.name());
        view.put("description", domainProcessDoc.description().description().orElse(""));

        DomainProcessSteps domainProcessSteps = domainProcessStepsFactory.buildDomainProcessSteps(domainProcessDoc,
                domain);
        view.put("steps", domainProcessSteps.orderedSteps().stream()
                .filter(step -> !step.componentDoc().description().trivial())
                .filter(step -> !step.external())
                .map(this::adapt)
                .collect(toList()));

        return view;
    }

    private DomainProcessStepsFactory domainProcessStepsFactory;

    private HashMap<String, Object> adapt(Step step) {
        HashMap<String, Object> view = new HashMap<>();
        view.put("name", step.componentDoc().name());
        view.put("description", step.componentDoc().description().description().orElse(""));
        return view;
    }

    private HashMap<String, Object> adapt(UbiquitousLanguageEntry entry) {
        HashMap<String, Object> view = new HashMap<>();
        view.put("name", entry.qualifiedName());
        view.put("type", entry.getType());
        view.put("description", entry.componentDoc().shortDescriptionOrDefault());
        return view;
    }

    private void copyCss()
            throws IOException {
        IOUtils.copy(getClass().getResourceAsStream("/style.css"),
                        new FileOutputStream(new File(configuration.outputDirectory(), "style.css")));
    }

    private UbiquitousLanguageFactory ubitquitousLanguageFactory;
}
