package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import poussecafe.doc.doclet.PousseCafeDocletConfiguration;
import poussecafe.doc.model.aggregatedoc.AggregateDoc;
import poussecafe.doc.model.aggregatedoc.AggregateDocRepository;
import poussecafe.doc.model.domainprocessdoc.DomainProcessDoc;
import poussecafe.doc.model.domainprocessdoc.DomainProcessDocRepository;
import poussecafe.doc.model.entitydoc.EntityDoc;
import poussecafe.doc.model.entitydoc.EntityDocId;
import poussecafe.doc.model.entitydoc.EntityDocRepository;
import poussecafe.doc.model.moduledoc.ModuleDoc;
import poussecafe.doc.model.moduledoc.ModuleDocRepository;
import poussecafe.doc.model.processstepdoc.ProcessStepDoc;
import poussecafe.doc.model.processstepdoc.ProcessStepDocRepository;
import poussecafe.doc.model.relationdoc.ComponentType;
import poussecafe.doc.model.relationdoc.RelationDoc;
import poussecafe.doc.model.relationdoc.RelationDocRepository;
import poussecafe.doc.model.servicedoc.ServiceDoc;
import poussecafe.doc.model.servicedoc.ServiceDocRepository;
import poussecafe.doc.model.vodoc.ValueObjectDoc;
import poussecafe.doc.model.vodoc.ValueObjectDocId;
import poussecafe.doc.model.vodoc.ValueObjectDocRepository;
import poussecafe.domain.Service;
import poussecafe.source.analysis.ClassName;

import static java.util.stream.Collectors.toList;

public class DomainFactory implements Service {

    public Domain buildDomain() {
        return new Domain.Builder()
                .name(configuration.generationConfiguration().domainName())
                .version(configuration.generationConfiguration().version())
                .modules(modules())
                .relations(relations())
                .build();
    }

    private PousseCafeDocletConfiguration configuration;

    private List<Module> modules() {
        List<Module> modules = new ArrayList<>();
        for(ModuleDoc moduleDoc : moduleDocRepository.findAll()) {
            modules.add(module(moduleDoc));
        }
        return modules;
    }

    private ModuleDocRepository moduleDocRepository;

    private Module module(ModuleDoc moduleDoc) {
        return new Module.Builder()
                .documentation(moduleDoc.toDocumentationItem())
                .aggregates(aggregates(moduleDoc))
                .services(services(moduleDoc).stream().map(ServiceDoc::toDocumentationItem).collect(toList()))
                .processes(processes(moduleDoc))
                .listeners(listeners(moduleDoc))
                .build();
    }

    private List<Aggregate> aggregates(ModuleDoc moduleDoc) {
        List<Aggregate> aggregates = new ArrayList<>();
        for(AggregateDoc aggregateDoc : aggregateDocRepository.findByModule(moduleDoc.attributes().identifier().value())) {
            aggregates.add(aggregate(aggregateDoc));
        }
        return aggregates;
    }

    private AggregateDocRepository aggregateDocRepository;

    private Aggregate aggregate(AggregateDoc aggregateDoc) {
        return new Aggregate.Builder()
                .documentation(aggregateDoc.toDocumentationItem())
                .entities(entities(aggregateDoc))
                .valueObjects(valueObjects(aggregateDoc))
                .build();
    }

    private List<DocumentationItem> entities(AggregateDoc aggregateDoc) {
        return findEntities(aggregateDoc.className()).stream()
                .map(entityDocRepository::getOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(EntityDoc::toDocumentationItem)
                .collect(toList());
    }

    private Set<EntityDocId> findEntities(ClassName fromClassName) {
        return findEntities(fromClassName, new HashSet<>());
    }

    private Set<EntityDocId> findEntities(ClassName fromClassName, Set<ClassName> exploredClasses) {
        Set<EntityDocId> ids = new HashSet<>();
        if(!exploredClasses.contains(fromClassName)) {
            exploredClasses.add(fromClassName);
            for(RelationDoc relation : relationRepository.findWithFromClassName(fromClassName)) {
                if(relation.toComponent().type() == ComponentType.ENTITY) {
                    ids.add(EntityDocId.ofClassName(relation.toComponent().className().toString()));
                }
                if(relation.toComponent().type() != ComponentType.AGGREGATE) {
                    ids.addAll(findEntities(relation.toComponent().className(), exploredClasses));
                }
            }
        }
        return ids;
    }

    private RelationDocRepository relationRepository;

    private EntityDocRepository entityDocRepository;

    private List<DocumentationItem> valueObjects(AggregateDoc aggregateDoc) {
        return findValueObjects(aggregateDoc.className()).stream()
                .map(valueObjectDocRepository::getOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ValueObjectDoc::toDocumentationItem)
                .collect(toList());
    }

    private Set<ValueObjectDocId> findValueObjects(ClassName fromClassName) {
        return findValueObjects(fromClassName, new HashSet<>());
    }

    private Set<ValueObjectDocId> findValueObjects(ClassName fromClassName, Set<ClassName> exploredClassNames) {
        Set<ValueObjectDocId> ids = new HashSet<>();
        if(!exploredClassNames.contains(fromClassName)) {
            exploredClassNames.add(fromClassName);
            for(RelationDoc relation : relationRepository.findWithFromClassName(fromClassName)) {
                if(relation.toComponent().type() == ComponentType.VALUE_OBJECT) {
                    ids.add(ValueObjectDocId.ofClassName(relation.toComponent().className().toString()));
                }
                if(relation.toComponent().type() != ComponentType.AGGREGATE) {
                    ids.addAll(findValueObjects(relation.toComponent().className(), exploredClassNames));
                }
            }
        }
        return ids;
    }

    private ValueObjectDocRepository valueObjectDocRepository;

    private List<ServiceDoc> services(ModuleDoc moduleDoc) {
        return serviceDocRepository.findByModuleId(moduleDoc.attributes().identifier().value());
    }

    private ServiceDocRepository serviceDocRepository;

    private List<DocumentationItem> processes(ModuleDoc moduleDoc) {
        return domainProcessDocRepository.findByModuleId(moduleDoc.attributes().identifier().value()).stream()
                .map(DomainProcessDoc::toDocumentationItem)
                .collect(toList());
    }

    private DomainProcessDocRepository domainProcessDocRepository;

    private List<MessageListener> listeners(ModuleDoc moduleDoc) {
        return processStepDocRepository.findByModule(moduleDoc.attributes().identifier().value()).stream()
                .map(ProcessStepDoc::toMessageListener)
                .collect(toList());
    }

    private ProcessStepDocRepository processStepDocRepository;

    private List<Relation> relations() {
        return relationDocRepository.findAll().stream()
                .map(RelationDoc::toRelation)
                .collect(toList());
    }

    private RelationDocRepository relationDocRepository;
}