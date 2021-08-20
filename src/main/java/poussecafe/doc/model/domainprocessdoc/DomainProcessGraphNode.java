package poussecafe.doc.model.domainprocessdoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.domain.ValueObject;

public class DomainProcessGraphNode implements ValueObject {

    private DocumentationItem componentDoc;

    public DocumentationItem componentDoc() {
        return componentDoc;
    }

    private List<ToStep> tos = new ArrayList<>();

    public List<ToStep> tos() {
        return Collections.unmodifiableList(tos);
    }

    private String consumedEvent;

    public Optional<String> consumedEvent() {
        return Optional.ofNullable(consumedEvent);
    }

    private boolean external;

    public boolean external() {
        return external;
    }

    public DomainProcessGraphNodeName stepName() {
        return new DomainProcessGraphNodeName(componentDoc.name());
    }

    public static class Builder {

        private DomainProcessGraphNode step = new DomainProcessGraphNode();

        public Builder componentDoc(DocumentationItem componentDoc) {
            step.componentDoc = componentDoc;
            return this;
        }

        public Builder to(ToStep to) {
            Objects.requireNonNull(to);
            step.tos.add(to);
            return this;
        }

        public Builder tos(List<ToStep> tos) {
            step.tos.addAll(tos);
            return this;
        }

        public Builder consumedEvent(String consumedEvent) {
            return consumedEvent(Optional.of(consumedEvent));
        }

        public Builder consumedEvent(Optional<String> consumedEvent) {
            step.consumedEvent = consumedEvent.orElse(null);
            return this;
        }

        public Builder external(boolean external) {
            step.external = external;
            return this;
        }

        public Builder step(DomainProcessGraphNode otherStep) {
            step.componentDoc = otherStep.componentDoc;
            step.tos.addAll(otherStep.tos);
            step.consumedEvent = otherStep.consumedEvent;
            step.external = otherStep.external;
            return this;
        }

        public DomainProcessGraphNode build() {
            Objects.requireNonNull(step.componentDoc);
            return step;
        }
    }

    private DomainProcessGraphNode() {

    }
}
