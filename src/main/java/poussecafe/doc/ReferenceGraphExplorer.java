package poussecafe.doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import poussecafe.source.analysis.ClassName;
import poussecafe.source.model.ComponentType;
import poussecafe.source.model.SourceModel;
import poussecafe.source.model.TypeComponent;
import poussecafe.source.model.TypeReference;

import static java.util.Objects.requireNonNull;

public class ReferenceGraphExplorer {

    public void explore() {
        explore(entryPoint);
    }

    private void explore(List<TypeReference> references) {
        references.forEach(this::explore);
    }

    private List<TypeReference> entryPoint;

    private Predicate<TypeReference> matcher;

    public List<TypeReference> matches() {
        return Collections.unmodifiableList(matches);
    }

    private List<TypeReference> matches = new ArrayList<>();

    private void explore(TypeReference reference) {
        if(!alreadyExplored.contains(reference)) {
            alreadyExplored.add(reference);
            if(matcher.test(reference)) {
                matches.add(reference);
            }
            var nextReferences = getReferences(reference);
            explore(nextReferences);
        }
    }

    private List<TypeReference> getReferences(TypeReference reference) {
        if(reference.type() == ComponentType.VALUE_OBJECT) {
            return typeReferences(reference.typeClassName(), model.valueObjects());
        } else if(reference.type() == ComponentType.ENTITY) {
            return typeReferences(reference.typeClassName(), model.entities());
        } else {
            throw new IllegalArgumentException("Unsupported component type " + reference.type());
        }
    }

    private List<TypeReference> typeReferences(ClassName expectedClassName, List<TypeComponent> components) {
        var valueObject = components.stream().filter(candidate -> expectedClassName.equals(candidate.typeName().asName())).findFirst().orElseThrow();
        return valueObject.references();
    }

    private Set<TypeReference> alreadyExplored = new HashSet<>();

    private SourceModel model;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        public ReferenceGraphExplorer build() {
            requireNonNull(explorer.model);
            requireNonNull(explorer.entryPoint);
            requireNonNull(explorer.matcher);
            return explorer;
        }

        private ReferenceGraphExplorer explorer = new ReferenceGraphExplorer();

        public Builder entryPoint(List<TypeReference> entryPoint) {
            explorer.entryPoint = new ArrayList<>();
            explorer.entryPoint.addAll(entryPoint);
            return this;
        }

        public Builder matcher(Predicate<TypeReference> matcher) {
            explorer.matcher = matcher;
            return this;
        }

        public Builder model(SourceModel model) {
            explorer.model = model;
            return this;
        }

        private Builder() {

        }
    }

    private ReferenceGraphExplorer() {

    }
}
