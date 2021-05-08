package poussecafe.doc.model.relationdoc;

import java.util.List;
import poussecafe.domain.EntityDataAccess;

public interface RelationDataAccess<D extends RelationDoc.Attributes> extends EntityDataAccess<RelationId, D> {

    List<D> findWithFromClass(String className);

}
