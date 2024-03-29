package poussecafe.doc.model.servicedoc;

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
  factory = ServiceDocFactory.class,
  repository = ServiceDocRepository.class
)
public class ServiceDoc extends AggregateRoot<ServiceDocId, ServiceDoc.Attributes> {

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

    public static interface Attributes extends EntityAttributes<ServiceDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();
    }
}
