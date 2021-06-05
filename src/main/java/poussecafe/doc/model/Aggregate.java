package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Aggregate {

    public static class Builder {

        private Aggregate aggregate = new Aggregate();

        public Builder documentation(DocumentationItem documentation) {
            aggregate.documentation = documentation;
            return this;
        }

        public Builder entities(List<DocumentationItem> entities) {
            aggregate.entities = new ArrayList<>(entities);
            return this;
        }

        public Builder valueObjects(List<DocumentationItem> valueObjects) {
            aggregate.valueObjects = new ArrayList<>(valueObjects);
            return this;
        }

        public Builder processSteps(List<DocumentationItem> processSteps) {
            aggregate.processSteps = new ArrayList<>(processSteps);
            return this;
        }

        public Aggregate build() {
            Objects.requireNonNull(aggregate.documentation);
            Objects.requireNonNull(aggregate.entities);
            Objects.requireNonNull(aggregate.valueObjects);
            Objects.requireNonNull(aggregate.processSteps);
            return aggregate;
        }
    }

    private Aggregate() {

    }

    private DocumentationItem documentation;

    public DocumentationItem documentation() {
        return documentation;
    }

    private List<DocumentationItem> entities;

    public List<DocumentationItem> entities() {
        return entities;
    }

    private List<DocumentationItem> valueObjects;

    public List<DocumentationItem> valueObjects() {
        return valueObjects;
    }

    private List<DocumentationItem> processSteps;

    public List<DocumentationItem> processSteps() {
        return processSteps;
    }
}
