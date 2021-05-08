package poussecafe.doc.model.vodoc;

import java.util.Optional;
import poussecafe.attribute.Attribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.DocumentationItem;
import poussecafe.doc.StringNormalizer;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;
import poussecafe.source.analysis.ClassName;

/**
 * <p>ValueObjectDoc describes the documentation of a Value Object in a given Bounded Context.</p>
 */
@Aggregate(
  factory = ValueObjectDocFactory.class,
  repository = ValueObjectDocRepository.class
)
public class ValueObjectDoc extends AggregateRoot<ValueObjectDocId, ValueObjectDoc.Attributes> {

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

    public static interface Attributes extends EntityAttributes<ValueObjectDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();
    }
}
