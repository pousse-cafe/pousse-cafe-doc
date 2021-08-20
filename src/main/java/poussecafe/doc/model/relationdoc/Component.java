package poussecafe.doc.model.relationdoc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import poussecafe.domain.ValueObject;
import poussecafe.source.analysis.ClassName;

import static java.util.Objects.requireNonNull;
import static poussecafe.util.Equality.referenceEquals;

public class Component implements ValueObject {

    public Component(ComponentType type, ClassName className, String name) {
        requireNonNull(type);
        this.type = type;
        requireNonNull(className);
        this.className = className;
        requireNonNull(name);
        this.name = name;
    }

    private ComponentType type;

    public ComponentType type() {
        return type;
    }

    private ClassName className;

    public ClassName className() {
        return className;
    }

    public String name() {
        return name;
    }

    private String name;

    @Override
    public boolean equals(Object obj) {
        return referenceEquals(this, obj).orElse(other -> new EqualsBuilder()
                .append(type, other.type)
                .append(className, other.className)
                .append(name, other.name)
                .build());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(type)
                .append(className)
                .append(name)
                .build();
    }
}
