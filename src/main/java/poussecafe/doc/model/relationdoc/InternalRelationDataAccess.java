package poussecafe.doc.model.relationdoc;

import java.util.List;
import poussecafe.discovery.DataAccessImplementation;
import poussecafe.storage.internal.InternalDataAccess;
import poussecafe.storage.internal.InternalStorage;

import static java.util.stream.Collectors.toList;

@DataAccessImplementation(
    aggregateRoot = RelationDoc.class,
    dataImplementation = RelationData.class,
    storageName = InternalStorage.NAME
)
public class InternalRelationDataAccess extends InternalDataAccess<RelationId, RelationData> implements RelationDataAccess<RelationData> {

    @Override
    public List<RelationData> findWithFromClass(String className) {
        return findAll().stream()
                .filter(data -> data.identifier().value().fromClass().toString().equals(className))
                .collect(toList());
    }

}
