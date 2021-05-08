package poussecafe.doc.model.relationdoc;

import java.util.Objects;
import poussecafe.domain.ValueObject;
import poussecafe.source.analysis.ClassName;

public class RelationId implements ValueObject {

    public RelationId(ClassName fromClass, ClassName toClass) {
        Objects.requireNonNull(fromClass);
        this.fromClass = fromClass;

        Objects.requireNonNull(toClass);
        this.toClass = toClass;
    }

    private ClassName fromClass;

    public ClassName fromClass() {
        return fromClass;
    }

    private ClassName toClass;

    public ClassName toClass() {
        return toClass;
    }
}
