package poussecafe.doc.model.relationdoc;

import poussecafe.domain.AggregateFactory;

public class RelationDocFactory extends AggregateFactory<RelationId, RelationDoc, RelationDoc.Attributes> {

    public RelationDoc newRelation(NewRelationParameters parameters) {
        RelationDoc relation = newAggregateWithId(new RelationId(parameters.fromComponent.className(),
                parameters.toComponent.className()));

        relation.attributes().fromType().value(parameters.fromComponent.type());
        relation.attributes().fromName().value(parameters.fromComponent.name());

        relation.attributes().toType().value(parameters.toComponent.type());
        relation.attributes().toName().value(parameters.toComponent.name());

        return relation;
    }

    public static class NewRelationParameters {

        public Component fromComponent;

        public Component toComponent;
    }
}
