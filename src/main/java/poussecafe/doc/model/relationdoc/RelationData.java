package poussecafe.doc.model.relationdoc;

import java.io.Serializable;
import poussecafe.attribute.Attribute;
import poussecafe.source.analysis.ClassName;

import static poussecafe.attribute.AttributeBuilder.single;

@SuppressWarnings("serial")
public class RelationData implements RelationDoc.Attributes, Serializable {

    @Override
    public Attribute<RelationId> identifier() {
        return new Attribute<>() {
            @Override
            public RelationId value() {
                return new RelationId(new ClassName(fromClass), new ClassName(toClass));
            }

            @Override
            public void value(RelationId value) {
                fromClass = value.fromClass().toString();
                toClass = value.toClass().toString();
            }
        };
    }

    private String fromClass;

    private String toClass;

    @Override
    public Attribute<ComponentType> fromType() {
        return new Attribute<>() {
            @Override
            public ComponentType value() {
                return fromType;
            }

            @Override
            public void value(ComponentType value) {
                fromType = value;
            };
        };
    }

    private ComponentType fromType;

    @Override
    public Attribute<ComponentType> toType() {
        return new Attribute<>() {
            @Override
            public ComponentType value() {
                return toType;
            }

            @Override
            public void value(ComponentType value) {
                toType = value;
            };
        };
    }

    private ComponentType toType;

    @Override
    public Attribute<String> fromName() {
        return single(String.class)
                .read(() -> fromName)
                .write(value -> fromName = value)
                .build();
    }

    private String fromName;

    @Override
    public Attribute<String> toName() {
        return single(String.class)
                .read(() -> toName)
                .write(value -> toName = value)
                .build();
    }

    private String toName;
}
