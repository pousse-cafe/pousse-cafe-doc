package poussecafe.doc.model.entitydoc;

import java.util.Optional;
import poussecafe.attribute.Attribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.DocumentationItem;
import poussecafe.doc.StringNormalizer;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;
import poussecafe.source.analysis.ClassName;

@Aggregate(
  factory = EntityDocFactory.class,
  repository = EntityDocRepository.class
)
public class EntityDoc extends AggregateRoot<EntityDocId, EntityDoc.Attributes> {

    void idClassName(String idClassName) {
        attributes().idClassName().value(idClassName);
    }

    public String id() {
        return StringNormalizer.normalizeString(attributes().moduleComponentDoc().value().componentDoc().name());
    }

    public DocumentationItem toDocumentationItem() {
        return attributes().moduleComponentDoc().value().toDocumentationItem()
                .id(id())
                .className(Optional.of(className()))
                .build();
    }

    public ClassName className() {
        return new ClassName(attributes().identifier().value().stringValue());
    }

    public static interface Attributes extends EntityAttributes<EntityDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();

        Attribute<String> idClassName();
    }
}
