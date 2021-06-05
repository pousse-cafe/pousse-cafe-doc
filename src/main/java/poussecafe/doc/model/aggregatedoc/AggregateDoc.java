package poussecafe.doc.model.aggregatedoc;

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
    factory = AggregateDocFactory.class,
    repository = AggregateDocRepository.class
)
public class AggregateDoc extends AggregateRoot<AggregateDocId, AggregateDoc.Attributes> {

    public String id() {
        return StringNormalizer.normalizeString(attributes().moduleComponentDoc().value().componentDoc().name());
    }

    public ClassName className() {
        return new ClassName(attributes().identifier().value().stringValue());
    }

    public DocumentationItem toDocumentationItem() {
        return attributes().moduleComponentDoc().value().toDocumentationItem()
                .id(id())
                .className(Optional.of(className()))
                .build();
    }

    public static interface Attributes extends EntityAttributes<AggregateDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();

        Attribute<String> idClassName();
    }
}
