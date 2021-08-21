package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import poussecafe.discovery.DefaultProcess;
import poussecafe.doc.StringNormalizer;
import poussecafe.domain.ValueObject;
import poussecafe.source.analysis.ClassName;
import poussecafe.source.model.Documentation;

import static java.util.stream.Collectors.toList;

public class Module implements ValueObject {

    private DocumentationItem documentation;

    public DocumentationItem documentation() {
        return documentation;
    }

    private List<Aggregate> aggregates;

    public List<Aggregate> aggregates() {
        return aggregates;
    }

    public Optional<Aggregate> aggregate(String name) {
        return aggregates().stream().filter(item -> item.documentation().name().equals(name)).findFirst();
    }

    private List<DocumentationItem> services;

    public List<DocumentationItem> services() {
        return services;
    }

    private List<DocumentationItem> processes;

    public List<DocumentationItem> processes() {
        return processes;
    }

    public boolean isEmpty() {
        return aggregates.isEmpty()
                && services.isEmpty()
                && processes.isEmpty();
    }

    public List<MessageListener> listeners() {
        return Collections.unmodifiableList(listeners);
    }

    private List<MessageListener> listeners = new ArrayList<>();

    public List<MessageListener> listeners(String processName) {
        return listeners.stream()
                .filter(item -> item.processNames().contains(processName))
                .collect(toList());
    }

    public static class Builder {

        private Module module = new Module();

        public Builder documentation(DocumentationItem documentation) {
            module.documentation = documentation;
            return this;
        }

        public Builder aggregates(List<Aggregate> aggregates) {
            module.aggregates = new ArrayList<>(aggregates);
            return this;
        }

        public Builder services(List<DocumentationItem> services) {
            module.services = new ArrayList<>(services);
            return this;
        }

        public Builder processes(List<DocumentationItem> processes) {
            module.processes = new ArrayList<>(processes);
            return this;
        }

        public Builder listeners(List<MessageListener> listeners) {
            module.listeners.addAll(listeners);
            return this;
        }

        public Module build() {
            Objects.requireNonNull(module.documentation);
            Objects.requireNonNull(module.aggregates);
            Objects.requireNonNull(module.services);
            Objects.requireNonNull(module.processes);

            if(module.listeners.stream().anyMatch(listener ->
                listener.processNames().contains(DefaultProcess.class.getSimpleName()))) {
                module.processes.add(new DocumentationItem.Builder()
                        .id(StringNormalizer.normalizeString(DefaultProcess.class.getSimpleName()))
                        .className(Optional.of(new ClassName(DefaultProcess.class.getCanonicalName())))
                        .description(new Documentation.Builder()
                                .description("The default process includes all listeners with no explicit process defined. One may explictly link a listener to the default process but this is not recommended.")
                                .shortDescription("The default process includes all listeners with no explicit process defined")
                                .build())
                        .moduleName(module.documentation.moduleName())
                        .name(DefaultProcess.class.getSimpleName())
                        .build());
            }

            return module;
        }
    }

    private Module() {

    }
}
