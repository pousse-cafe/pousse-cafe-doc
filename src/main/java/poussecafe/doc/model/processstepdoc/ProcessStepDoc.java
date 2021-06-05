package poussecafe.doc.model.processstepdoc;

import java.util.List;
import poussecafe.attribute.Attribute;
import poussecafe.attribute.MapAttribute;
import poussecafe.attribute.OptionalAttribute;
import poussecafe.attribute.SetAttribute;
import poussecafe.discovery.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.MessageListener;
import poussecafe.doc.model.ModuleComponentDoc;
import poussecafe.doc.model.aggregatedoc.AggregateDocId;
import poussecafe.domain.AggregateRoot;
import poussecafe.domain.EntityAttributes;

@Aggregate(
    factory = ProcessStepDocFactory.class,
    repository = ProcessStepDocRepository.class
)
public class ProcessStepDoc extends AggregateRoot<ProcessStepDocId, ProcessStepDoc.Attributes> {

    public DocumentationItem toDocumentationItem() {
        return attributes().moduleComponentDoc().value().toDocumentationItem()
                .id(attributes().identifier().value().stringValue())
                .build();
    }

    public MessageListener toMessageListener() {
        return new MessageListener.Builder()
                .documentation(toDocumentationItem())
                .fromExternals(attributes().fromExternals().value())
                .processNames(attributes().processNames().value())
                .producedEvents(attributes().producedEvents().value())
                .stepMethodSignature(attributes().stepMethodSignature().value())
                .toExternals(attributes().toExternals().value())
                .toExternalsByEvent(attributes().toExternalsByEvent().value())
                .aggregate(attributes().aggregateName().value())
                .build();
    }

    public static interface Attributes extends EntityAttributes<ProcessStepDocId> {

        Attribute<ModuleComponentDoc> moduleComponentDoc();

        SetAttribute<String> processNames();

        OptionalAttribute<StepMethodSignature> stepMethodSignature();

        SetAttribute<NameRequired> producedEvents();

        SetAttribute<String> toExternals();

        MapAttribute<NameRequired, List<String>> toExternalsByEvent();

        SetAttribute<String> fromExternals();

        OptionalAttribute<AggregateDocId> aggregate();

        OptionalAttribute<String> aggregateName();
    }
}
