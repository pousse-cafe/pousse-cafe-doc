package poussecafe.doc.model.servicedoc;

import javax.lang.model.element.TypeElement;
import poussecafe.doc.ClassDocPredicates;
import poussecafe.doc.model.ComponentDocFactory;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.doc.model.moduledoc.ModuleDocId;
import poussecafe.doc.model.moduledoc.ModuleDocRepository;
import poussecafe.domain.AggregateFactory;
import poussecafe.domain.DomainException;
import poussecafe.domain.Service;

public class ServiceDocFactory extends AggregateFactory<ServiceDocId, ServiceDoc, ServiceDoc.Attributes> {

    public ServiceDoc newServiceDoc(ModuleDocId moduleDocId, TypeElement classDoc) {
        if(!isServiceDoc(classDoc)) {
            throw new DomainException("Class " + classDoc.getQualifiedName() + " is not a service");
        }

        String name = classDoc.getSimpleName().toString();
        ServiceDocId id = ServiceDocId.ofClassName(classDoc.getQualifiedName().toString());
        ServiceDoc serviceDoc = newAggregateWithId(id);
        String moduleName = moduleDocRepository.get(moduleDocId).attributes().componentDoc().value().name();
        serviceDoc.attributes().moduleComponentDoc().value(new ModuleComponentDoc.Builder()
                .moduleDocId(moduleDocId)
                .moduleName(moduleName)
                .componentDoc(componentDocFactory.buildDoc(name, classDoc))
                .build());
        return serviceDoc;
    }

    private ComponentDocFactory componentDocFactory;

    private ModuleDocRepository moduleDocRepository;

    public boolean isServiceDoc(TypeElement classDoc) {
        return classDocPredicates.documentsWithSuperinterface(classDoc, Service.class);
    }

    private ClassDocPredicates classDocPredicates;
}
