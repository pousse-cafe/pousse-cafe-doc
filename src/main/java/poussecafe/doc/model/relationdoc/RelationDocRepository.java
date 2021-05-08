package poussecafe.doc.model.relationdoc;

import java.util.List;
import poussecafe.domain.AggregateRepository;
import poussecafe.source.analysis.ClassName;

public class RelationDocRepository extends AggregateRepository<RelationId, RelationDoc, RelationDoc.Attributes> {

    public List<RelationDoc> findWithFromClassName(ClassName className) {
        return wrap(dataAccess().findWithFromClass(className.toString()));
    }

    @Override
    public RelationDataAccess<RelationDoc.Attributes> dataAccess() {
        return (RelationDataAccess<RelationDoc.Attributes>) super.dataAccess();
    }

    public List<RelationDoc> findAll() {
        return wrap(dataAccess().findAll());
    }
}
