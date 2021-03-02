package poussecafe.doc.model.domainprocessdoc;

import poussecafe.attribute.Attribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.StringNormalizer;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;

@Aggregate(
  factory = DomainProcessDocFactory.class,
  repository = DomainProcessDocRepository.class
)
public class DomainProcessDoc extends AggregateRoot<DomainProcessDocId, DomainProcessDoc.Attributes> {

    public String id() {
        return StringNormalizer.normalizeString(attributes().moduleComponentDoc().value().componentDoc().name());
    }

    public static interface Attributes extends EntityAttributes<DomainProcessDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();
    }
}
