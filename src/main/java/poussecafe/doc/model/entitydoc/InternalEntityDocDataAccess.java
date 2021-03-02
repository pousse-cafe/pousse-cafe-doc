package poussecafe.doc.model.entitydoc;

import poussecafe.discovery.DataAccessImplementation;
import poussecafe.storage.internal.InternalDataAccess;
import poussecafe.storage.internal.InternalStorage;

@DataAccessImplementation(
    aggregateRoot = EntityDoc.class,
    dataImplementation = EntityDocData.class,
    storageName = InternalStorage.NAME
)
public class InternalEntityDocDataAccess extends InternalDataAccess<EntityDocId, EntityDocData> implements EntityDocDataAccess<EntityDocData> {

}
