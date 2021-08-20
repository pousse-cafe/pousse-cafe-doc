package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import poussecafe.doc.model.processstepdoc.NameRequired;
import poussecafe.doc.model.processstepdoc.StepMethodSignature;

import static java.util.Objects.requireNonNull;

public class MessageListener {

    public Optional<StepMethodSignature> stepMethodSignature() {
        return Optional.ofNullable(stepMethodSignature);
    }

    private StepMethodSignature stepMethodSignature;

    public DocumentationItem documentation() {
        return documentation;
    }

    private DocumentationItem documentation;

    public Set<NameRequired> producedEvents() {
        return Collections.unmodifiableSet(producedEvents);
    }

    private Set<NameRequired> producedEvents = new HashSet<>();

    @Deprecated
    public List<String> toExternals() {
        return Collections.unmodifiableList(toExternals);
    }

    /**
     * @deprecated replaced by toExternalsByEvent
     */
    @Deprecated
    private List<String> toExternals = new ArrayList<>();

    public Map<NameRequired, List<String>> toExternalsByEvent() {
        return Collections.unmodifiableMap(toExternalsByEvent);
    }

    private Map<NameRequired, List<String>> toExternalsByEvent = new HashMap<>();

    public Set<String> processNames() {
        return Collections.unmodifiableSet(processNames);
    }

    private Set<String> processNames = new HashSet<>();

    public List<String> fromExternals() {
        return Collections.unmodifiableList(fromExternals);
    }

    private List<String> fromExternals = new ArrayList<>();

    public Optional<String> aggregate() {
        return Optional.ofNullable(aggregate);
    }

    private String aggregate;

    public static class Builder {

        public MessageListener build() {
            requireNonNull(listener.documentation);
            return listener;
        }

        private MessageListener listener = new MessageListener();

        public Builder stepMethodSignature(Optional<StepMethodSignature> stepMethodSignature) {
            listener.stepMethodSignature = stepMethodSignature.orElse(null);
            return this;
        }

        public Builder documentation(DocumentationItem documentation) {
            listener.documentation = documentation;
            return this;
        }

        public Builder producedEvents(Set<NameRequired> producedEvents) {
            listener.producedEvents.addAll(producedEvents);
            return this;
        }

        @Deprecated
        public Builder toExternals(Collection<String> toExternals) {
            listener.toExternals.addAll(toExternals);
            return this;
        }

        public Builder toExternalsByEvent(Map<NameRequired, List<String>> toExternalsByEvent) {
            listener.toExternalsByEvent.putAll(toExternalsByEvent);
            return this;
        }

        public Builder processNames(Set<String> processNames) {
            listener.processNames.addAll(processNames);
            return this;
        }

        public Builder fromExternals(Collection<String> fromExternals) {
            listener.fromExternals.addAll(fromExternals);
            return this;
        }

        public Builder aggregate(Optional<String> aggregate) {
            listener.aggregate = aggregate.orElse(null);
            return this;
        }
    }

    private MessageListener() {

    }
}
