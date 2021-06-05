package poussecafe.doc.model.domainprocessdoc;

import java.util.Optional;
import poussecafe.attribute.Attribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.StringNormalizer;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;
import poussecafe.source.analysis.ClassName;

@Aggregate(
  factory = DomainProcessDocFactory.class,
  repository = DomainProcessDocRepository.class
)
public class DomainProcessDoc extends AggregateRoot<DomainProcessDocId, DomainProcessDoc.Attributes> {

    public String id() {
        return StringNormalizer.normalizeString(attributes().moduleComponentDoc().value().componentDoc().name());
    }

    public DocumentationItem toDocumentationItem() {
        return attributes().moduleComponentDoc().value().toDocumentationItem()
                .id(id())
                .className(Optional.of(new ClassName(className())))
                .build();
    }

    public String className() {
        return attributes().identifier().value().stringValue();
    }

    public static interface Attributes extends EntityAttributes<DomainProcessDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();
    }
}
