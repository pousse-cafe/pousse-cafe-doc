package poussecafe.doc.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import poussecafe.doc.DocumentationItem;
import poussecafe.domain.Service;

import static java.util.stream.Collectors.toList;

public class UbiquitousLanguageFactory implements Service {

    public List<UbiquitousLanguageEntry> buildUbiquitousLanguage(Domain domain) {
        Set<UbiquitousLanguageEntry> language = new HashSet<>();
        for(Module module : domain.modules()) {
            var moduleDoc = module.documentation();
            language.add(new UbiquitousLanguageEntry.Builder()
                            .componentDoc(moduleDoc)
                            .type("Module")
                            .build());

            String moduleName = moduleDoc.moduleName();
            for (Aggregate aggregate : module.aggregates()) {
                var aggregateDoc = aggregate.documentation();
                language
                        .add(new UbiquitousLanguageEntry.Builder()
                                .moduleName(moduleName)
                                .componentDoc(aggregateDoc)
                                .type("Aggregate")
                                .build());

                for (DocumentationItem entityDoc : aggregate.entities()) {
                    language
                            .add(new UbiquitousLanguageEntry.Builder()
                                    .moduleName(moduleName)
                                    .componentDoc(entityDoc)
                                    .type("Entity")
                                    .build());
                }

                for (DocumentationItem valueObjectDoc : aggregate.valueObjects()) {
                    language
                            .add(new UbiquitousLanguageEntry.Builder()
                                    .moduleName(moduleName)
                                    .componentDoc(valueObjectDoc)
                                    .type("Value Object")
                                    .build());
                }
            }

            for (DocumentationItem serviceDoc : module.services()) {
                language
                        .add(new UbiquitousLanguageEntry.Builder()
                                .moduleName(moduleName)
                                .componentDoc(serviceDoc)
                                .type("Service")
                                .build());
            }

            for (DocumentationItem domainProcessDoc : module.processes()) {
                language
                        .add(new UbiquitousLanguageEntry.Builder()
                                .moduleName(moduleName)
                                .componentDoc(domainProcessDoc)
                                .type("Domain Process")
                                .build());
            }
        }

        return language.stream().sorted().collect(toList());
    }
}
