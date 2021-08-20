package poussecafe.doc;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.MessageListener;
import poussecafe.doc.model.Module;
import poussecafe.doc.model.domainprocessdoc.ComponentMethodName;
import poussecafe.doc.model.processstepdoc.NameRequired;
import poussecafe.doc.model.processstepdoc.StepMethodSignature;
import poussecafe.source.model.ComponentType;
import poussecafe.source.model.ProcessModel;
import poussecafe.source.model.ProducedEvent;
import poussecafe.source.model.SourceModel;
import poussecafe.source.model.TypeComponent;
import poussecafe.source.model.TypeReference;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Builder
public class ModuleBuilder {

    public Module build() {
        var moduleClassName = module.typeName().asName();
        var id = StringNormalizer.normalizeString(moduleClassName.simple());
        var documentationItem =  new DocumentationItem.Builder()
                .className(Optional.of(moduleClassName))
                .description(module.documentation())
                .id(id)
                .name(moduleName())
                .moduleName(moduleName())
                .build();
        return new Module.Builder()
                .documentation(documentationItem)
                .aggregates(aggregates())
                .services(services())
                .listeners(listeners())
                .processes(processes())
                .build();
    }

    @NonNull
    private TypeComponent module;

    private List<Aggregate> aggregates() {
        return model.moduleAggregates(module)
                .map(this::buildAggregate)
                .collect(toList());
    }

    @NonNull
    private SourceModel model;

    private Aggregate buildAggregate(poussecafe.source.model.Aggregate sourceAggregate) {
        var moduleName = moduleName();
        var builder = new Aggregate.Builder();
        var id = StringNormalizer.normalizeString(sourceAggregate.name());
        var documentationItem = new DocumentationItem.Builder()
                .id(id)
                .className(Optional.of(sourceAggregate.className().asName()))
                .description(sourceAggregate.documentation())
                .moduleName(moduleName)
                .name(sourceAggregate.name())
                .build();
        return builder
                .documentation(documentationItem)
                .entities(entities(sourceAggregate))
                .valueObjects(valueObjects(sourceAggregate))
                .build();
    }

    private String moduleName() {
        return module.typeName().simpleName();
    }

    private List<DocumentationItem> entities(poussecafe.source.model.Aggregate sourceAggregate) {
        return sourceAggregate.rootReferences().stream()
                .filter(reference -> reference.type() == ComponentType.ENTITY)
                .map(this::entityDocumentation)
                .collect(toList());
    }

    private DocumentationItem entityDocumentation(TypeReference entityDocumentation) {
        return model.entities().stream()
                .filter(type -> type.typeName().asName().equals(entityDocumentation.typeClassName()))
                .findAny()
                .map(this::typeComponentDocumentation)
                .orElseThrow();
    }

    private DocumentationItem typeComponentDocumentation(TypeComponent component) {
        var componentName = component.typeName().simpleName();
        return new DocumentationItem.Builder()
                .id(StringNormalizer.normalizeString(componentName))
                .className(Optional.of(component.typeName().asName()))
                .description(component.documentation())
                .moduleName(moduleName())
                .name(componentName)
                .build();
    }

    private List<DocumentationItem> valueObjects(poussecafe.source.model.Aggregate sourceAggregate) {
        return sourceAggregate.rootReferences().stream()
                .filter(reference -> reference.type() == ComponentType.VALUE_OBJECT)
                .map(this::valueObjectDocumentation)
                .collect(toList());
    }

    private DocumentationItem valueObjectDocumentation(TypeReference entityDocumentation) {
        return model.valueObjects().stream()
                .filter(type -> type.typeName().asName().equals(entityDocumentation.typeClassName()))
                .findAny()
                .map(this::typeComponentDocumentation)
                .orElseThrow();
    }

    private List<DocumentationItem> services() {
        return model.services().stream()
                .map(this::typeComponentDocumentation)
                .collect(toList());
    }

    private List<MessageListener> listeners() {
        return model.messageListeners().stream()
                .map(this::listener)
                .collect(toList());
    }

    private MessageListener listener(poussecafe.source.model.MessageListener sourceListener) {
        var id = sourceListener.id();
        return new MessageListener.Builder()
                .aggregate(Optional.of(sourceListener.aggregateName()))
                .documentation(new DocumentationItem.Builder()
                        .id(StringNormalizer.normalizeString(id))
                        .className(Optional.empty())
                        .description(sourceListener.documentation())
                        .moduleName(moduleName())
                        .name(id)
                        .build())
                .fromExternals(sourceListener.consumesFromExternal())
                .processNames(new HashSet<>(sourceListener.processNames()))
                .producedEvents(sourceListener.producedEvents().stream().map(this::producedEvent).collect(toSet()))
                .stepMethodSignature(Optional.of(new StepMethodSignature.Builder()
                        .componentMethodName(new ComponentMethodName.Builder()
                                .componentName(sourceListener.aggregateName())
                                .methodName(sourceListener.methodName())
                                .build())
                        .consumedMessageName(Optional.of(sourceListener.consumedMessage().name()))
                        .build()))
                .toExternalsByEvent(sourceListener.producedEvents().stream()
                        .collect(toMap(this::producedEvent, ProducedEvent::consumedByExternal)))
                .build();
    }

    private NameRequired producedEvent(ProducedEvent event) {
        if(event.required()) {
            return NameRequired.required(event.message().name());
        } else {
            return NameRequired.optional(event.message().name());
        }
    }

    private List<DocumentationItem> processes() {
        return model.processes().stream()
                .map(ProcessModel::typeComponent)
                .map(this::typeComponentDocumentation)
                .collect(toList());
    }
}
