package poussecafe.doc.model.domainprocessdoc;

import java.util.List;
import poussecafe.discovery.DataAccessImplementation;
import poussecafe.doc.model.moduledoc.ModuleDocId;
import poussecafe.storage.internal.InternalDataAccess;
import poussecafe.storage.internal.InternalStorage;

import static java.util.stream.Collectors.toList;

@DataAccessImplementation(
    aggregateRoot = DomainProcessDoc.class,
    dataImplementation = DomainProcessDocData.class,
    storageName = InternalStorage.NAME
)
public class InternalDomainProcessDocDataAccess extends InternalDataAccess<DomainProcessDocId, DomainProcessDocData> implements DomainProcessDocDataAccess<DomainProcessDocData> {

    @Override
    public List<DomainProcessDocData> findByModuleId(ModuleDocId id) {
        return findAll().stream().filter(data -> data.moduleComponentDoc().value().moduleDocId().equals(id)).collect(toList());
    }
}
