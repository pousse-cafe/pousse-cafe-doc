package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import poussecafe.doc.model.processstepdoc.NameRequired;
import poussecafe.domain.ValueObject;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Domain implements ValueObject {

    private String name;

    public String name() {
        return name;
    }

    private String version;

    public String version() {
        return version;
    }

    private List<Module> modules;

    public List<Module> modules() {
        return modules;
    }

    public Optional<Module> module(String moduleName) {
        return modules.stream().filter(module -> module.documentation().name().equals(moduleName)).findFirst();
    }

    public List<MessageListener> listeners(String moduleName, String processName) {
        return module(moduleName)
                .map(module -> module.listeners(processName))
                .orElse(emptyList());
    }

    public List<MessageListener> findConsuming(String moduleName, String messageName) {
        return listeners(moduleName)
                .filter(item -> item.stepMethodSignature().isPresent())
                .filter(item -> item.stepMethodSignature().orElseThrow().consumedEventName().isPresent())
                .filter(item -> item.stepMethodSignature().orElseThrow().consumedEventName().orElseThrow().equals(messageName))
                .collect(toList());
    }

    public Stream<MessageListener> listeners(String moduleName) {
        return module(moduleName)
                .map(module -> module.listeners().stream())
                .orElse(Stream.empty());
    }

    public List<MessageListener> findProducing(String moduleName, String messageName) {
        return listeners(moduleName)
                .filter(item -> item.producedEvents().stream().map(NameRequired::name).collect(toSet()).contains(messageName))
                .collect(toList());
    }

    public List<Relation> relations() {
        return Collections.unmodifiableList(relations);
    }

    private List<Relation> relations = new ArrayList<>();

    public static class Builder {

        private Domain domain = new Domain();

        public Builder name(String name) {
            domain.name = name;
            return this;
        }

        public Builder version(String version) {
            domain.version = version;
            return this;
        }

        public Builder modules(List<Module> modules) {
            domain.modules = new ArrayList<>(modules);
            return this;
        }

        public Builder relations(List<Relation> relations) {
            domain.relations.addAll(relations);
            return this;
        }

        public Domain build() {
            Objects.requireNonNull(domain.name);
            Objects.requireNonNull(domain.version);
            Objects.requireNonNull(domain.modules);
            return domain;
        }
    }

    private Domain() {

    }
}
