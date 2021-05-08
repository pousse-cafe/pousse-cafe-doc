package poussecafe.doc.model.relationdoc;

import poussecafe.domain.ValueObject;
import poussecafe.source.analysis.ClassName;

import static java.util.Objects.requireNonNull;

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
}
