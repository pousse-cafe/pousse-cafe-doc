package poussecafe.doc.model.moduledoc;

import java.io.Serializable;
import poussecafe.attribute.Attribute;
import poussecafe.attribute.OptionalAttribute;
import poussecafe.doc.model.ComponentDoc;
import poussecafe.doc.model.ComponentDocData;

import static poussecafe.attribute.AttributeBuilder.optional;

@SuppressWarnings("serial")
public class ModuleDocData implements ModuleDoc.Attributes, Serializable {

    @Override
    public Attribute<ModuleDocId> identifier() {
        return new Attribute<>() {
            @Override
            public ModuleDocId value() {
                return ModuleDocId.ofPackageName(id);
            }

            @Override
            public void value(ModuleDocId value) {
                id = value.stringValue();
            }
        };
    }

    private String id;

    @Override
    public Attribute<ComponentDoc> componentDoc() {
        return new Attribute<>() {
            @Override
            public ComponentDoc value() {
                return componentDoc.toModel();
            }

            @Override
            public void value(ComponentDoc value) {
                componentDoc = ComponentDocData.of(value);
            }
        };
    }

    private ComponentDocData componentDoc;

    @Override
    public OptionalAttribute<String> className() {
        return optional(String.class)
                .read(() -> className)
                .write(value -> className = value)
                .build();
    }

    private String className;
}
