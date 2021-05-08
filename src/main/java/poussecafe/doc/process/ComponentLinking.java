package poussecafe.doc.process;

import poussecafe.doc.model.relationdoc.RelationDoc;
import poussecafe.doc.model.relationdoc.RelationDocFactory;
import poussecafe.doc.model.relationdoc.RelationDocRepository;
import poussecafe.doc.model.relationdoc.RelationDocFactory.NewRelationParameters;
import poussecafe.process.DomainProcess;

public class ComponentLinking extends DomainProcess {

    public void linkComponents(NewRelationParameters parameters) {
        RelationDoc relation = relationFactory.newRelation(parameters);
        if(relationRepository.getOptional(relation.attributes().identifier().value()).isEmpty()) {
            runInTransaction(RelationDoc.class, () -> relationRepository.add(relation));
        }
    }

    private RelationDocFactory relationFactory;

    private RelationDocRepository relationRepository;
}
