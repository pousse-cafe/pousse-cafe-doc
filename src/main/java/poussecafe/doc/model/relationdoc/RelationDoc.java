package poussecafe.doc.model.relationdoc;

import poussecafe.attribute.Attribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.model.Relation;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;

@Aggregate(
  factory = RelationDocFactory.class,
  repository = RelationDocRepository.class
)
public class RelationDoc extends AggregateRoot<RelationId, RelationDoc.Attributes> {

    public Component fromComponent() {
        return new Component(
                attributes().fromType().value(),
                attributes().identifier().value().fromClass(),
                attributes().fromName().value());
    }

    public Component toComponent() {
        return new Component(
                attributes().toType().value(),
                attributes().identifier().value().toClass(),
                attributes().toName().value());
    }

    public Relation toRelation() {
        return new Relation.Builder()
                .from(fromComponent())
                .to(toComponent())
                .build();
    }

    public static interface Attributes extends EntityAttributes<RelationId> {

        Attribute<ComponentType> fromType();

        Attribute<String> fromName();

        Attribute<ComponentType> toType();

        Attribute<String> toName();
    }
}
