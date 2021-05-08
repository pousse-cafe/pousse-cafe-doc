package poussecafe.doc;

import java.util.Optional;
import poussecafe.source.analysis.ClassName;
import poussecafe.source.model.Documentation;

import static java.util.Objects.requireNonNull;

public class DocumentationItem {

    public String id() {
        return id;
    }

    private String id;

    public Optional<ClassName> className() {
        return Optional.ofNullable(className);
    }

    private ClassName className;

    public String name() {
        return name;
    }

    private String name;

    public Documentation description() {
        return description;
    }

    private Documentation description;

    public String moduleName() {
        return moduleName;
    }

    private String moduleName;

    public String shortDescriptionOrDefault() {
        return description.shortDescription()
                .or(description::description)
                .map(string -> string.replaceAll("<[a-z\\/]+>", ""))
                .orElseThrow();
    }

    public static class Builder {

        public DocumentationItem build() {
            requireNonNull(item.id);
            requireNonNull(item.name);
            requireNonNull(item.description);
            return item;
        }

        private DocumentationItem item = new DocumentationItem();

        public Builder id(String id) {
            item.id = id;
            return this;
        }

        public Builder className(Optional<ClassName> className) {
            item.className = className.orElse(null);
            return this;
        }

        public Builder name(String name) {
            item.name = name;
            return this;
        }

        public Builder description(Documentation description) {
            item.description = description;
            return this;
        }

        public Builder moduleName(String moduleName) {
            item.moduleName = moduleName;
            return this;
        }
    }

    private DocumentationItem() {

    }
}
