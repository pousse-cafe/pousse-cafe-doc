package poussecafe.doc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.DomainProcessGraphNodes;
import poussecafe.doc.model.MessageListener;
import poussecafe.doc.model.MessageListenersPerEvent;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNode;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNodeName;
import poussecafe.doc.model.domainprocessdoc.ToStep;
import poussecafe.doc.model.processstepdoc.NameRequired;
import poussecafe.doc.model.processstepdoc.StepMethodSignature;
import poussecafe.source.model.Documentation;

import static java.util.stream.Collectors.toList;

public class DomainProcessStepsFactory {

    public static DomainProcessGraphNodes buildDomainProcessGraphNodes(DocumentationItem domainProcessDoc, Domain domain) {
        DomainProcessGraphNodes.Builder stepsBuilder = new DomainProcessGraphNodes.Builder();

        var moduleComponentDoc = domainProcessDoc;
        var moduleDocId = moduleComponentDoc.moduleName();
        String processName = moduleComponentDoc.name();

        List<MessageListener> listeners = domain.listeners(moduleDocId, processName);
        MessageListenersPerEvent eventToConsumingStepsMap = buildConsumingStepsPerEvent(listeners);

        Set<DomainProcessGraphNodeName> otherProcesses = new HashSet<>();
        for(MessageListener processStepDoc : listeners) {
            List<ToStep> currentStepToSteps = new ArrayList<>();

            List<DomainProcessGraphNodeName> toInternals = eventToConsumingStepsMap.locateToInternals(processStepDoc);
            currentStepToSteps.addAll(toDirectSteps(toInternals));

            Set<ToStep> toExternals = locateToExternals(processStepDoc);
            stepsBuilder.merge(toExternalStepsMap(toExternals));
            currentStepToSteps.addAll(toExternals);

            List<ToStep> toDomainProcesses = locateToDomainProcesses(domainProcessDoc, processStepDoc, domain);
            otherProcesses.addAll(toDomainProcesses.stream().map(ToStep::name).collect(toList()));
            stepsBuilder.merge(toExternalStepsMap(toDomainProcesses));
            currentStepToSteps.addAll(toDomainProcesses);

            var processStepComponentDoc = processStepDoc.documentation();
            DomainProcessGraphNode currentStep = new DomainProcessGraphNode.Builder()
                    .componentDoc(processStepComponentDoc)
                    .tos(currentStepToSteps)
                    .build();
            stepsBuilder.add(currentStep);

            DomainProcessGraphNodeName currentStepName = new DomainProcessGraphNodeName(processStepComponentDoc.name());
            ToStep toCurrentStep = directStep(currentStepName);

            List<DomainProcessGraphNodeName> fromExternals = locateFromExternals(processStepDoc);
            stepsBuilder.merge(fromExternalStepsMap(fromExternals, toCurrentStep));

            List<DomainProcessGraphNodeName> fromDomainProcesses = fromDomainProcesses(domainProcessDoc, processStepDoc, domain);
            otherProcesses.addAll(fromDomainProcesses);
            stepsBuilder.merge(fromExternalStepsMap(fromDomainProcesses, toCurrentStep));
        }

        Map<DomainProcessGraphNodeName, DomainProcessGraphNode> interprocessSteps = buildInterprocessSteps(moduleDocId, otherProcesses, domain);
        stepsBuilder.merge(interprocessSteps);

        return stepsBuilder.build();
    }

    private static List<DomainProcessGraphNodeName> locateFromExternals(MessageListener processStepDoc) {
        return processStepDoc.fromExternals().stream().map(DomainProcessGraphNodeName::new).collect(toList());
    }

    private static Map<DomainProcessGraphNodeName, DomainProcessGraphNode> fromExternalStepsMap(List<DomainProcessGraphNodeName> fromExternals, ToStep toCurrentStep) {
        Map<DomainProcessGraphNodeName, DomainProcessGraphNode> fromExternalSteps = new HashMap<>();
        for(DomainProcessGraphNodeName fromExternal : fromExternals) {
            var fromExternalStep = new DomainProcessGraphNode.Builder()
                    .componentDoc(new DocumentationItem.Builder()
                            .id("from" + fromExternal)
                            .name(fromExternal.stringValue())
                            .description(Documentation.empty())
                            .build())
                    .external(true)
                    .to(toCurrentStep)
                    .build();
            fromExternalSteps.put(fromExternalStep.stepName(), fromExternalStep);
        }
        return fromExternalSteps;
    }

    private static Map<DomainProcessGraphNodeName, DomainProcessGraphNode> toExternalStepsMap(Collection<ToStep> externalStepsNames) {
        Map<DomainProcessGraphNodeName, DomainProcessGraphNode> steps = new HashMap<>();
        for(ToStep externalToStep : externalStepsNames) {
            DomainProcessGraphNodeName externalStepName = externalToStep.name();
            steps.computeIfAbsent(externalStepName, key -> new DomainProcessGraphNode.Builder()
                    .componentDoc(new DocumentationItem.Builder()
                            .id("to" + externalStepName)
                            .name(externalStepName.stringValue())
                            .description(Documentation.empty())
                            .build())
                    .external(true)
                    .build());
        }
        return steps;
    }

    private static MessageListenersPerEvent buildConsumingStepsPerEvent(List<MessageListener> processStepDocs) {
        var builder = new MessageListenersPerEvent.Builder();
        for(MessageListener processStepDoc : processStepDocs) {
            builder.withMessageListener(processStepDoc);
        }
        return builder.build();
    }

    private static List<ToStep> toDirectSteps(Collection<DomainProcessGraphNodeName> tos) {
        List<ToStep> toSteps = new ArrayList<>();
        for(DomainProcessGraphNodeName to : tos) {
            toSteps.add(directStep(to));
        }
        return toSteps;
    }

    private static ToStep directStep(DomainProcessGraphNodeName to) {
        return new ToStep.Builder()
                .name(to)
                .directly(true)
                .build();
    }

    private static Set<ToStep> locateToExternals(MessageListener processStepDoc) {
        Set<ToStep> toExternals = new HashSet<>();
        toExternals.addAll(processStepDoc.toExternals().stream().map(DomainProcessGraphNodeName::new).map(DomainProcessStepsFactory::directStep).collect(toList()));
        for(Entry<NameRequired, List<String>> entry : processStepDoc.toExternalsByEvent().entrySet()) {
            boolean required = entry.getKey().required();
            toExternals.addAll(entry.getValue().stream().map(name -> toStep(name, required)).collect(toList()));
        }
        return toExternals;
    }

    private static ToStep toStep(String name, boolean required) {
        return new ToStep.Builder()
                .name(new DomainProcessGraphNodeName(name))
                .directly(required)
                .build();
    }

    private static List<ToStep> locateToDomainProcesses(
            DocumentationItem domainProcessDoc,
            MessageListener processStepDoc,
            Domain domain) {
        Set<NameRequired> producedEvents = processStepDoc.producedEvents();
        String domainProcessName = domainProcessDoc.name();
        String moduleDocId = processStepDoc.documentation().name();
        Set<ToStep> toDomainProcesses = new HashSet<>();
        for(NameRequired producedEvent : producedEvents) {
            for(MessageListener stepDoc : domain.findConsuming(moduleDocId, producedEvent.name())) {
                Set<String> processNames = stepDoc.processNames();
                for(String processName : processNames) {
                    if(!processName.equals(domainProcessName)) {
                        toDomainProcesses.add(new ToStep.Builder()
                                .name(new DomainProcessGraphNodeName(processName))
                                .directly(producedEvent.required())
                                .build());
                    }
                }
            }
        }
        return toDomainProcesses.stream().collect(toList());
    }

    private static List<DomainProcessGraphNodeName> fromDomainProcesses(
            DocumentationItem domainProcessDoc,
            MessageListener processStepDoc,
            Domain domain) {
        Optional<StepMethodSignature> stepMethodSignature = processStepDoc.stepMethodSignature();
        Optional<String> consumedEvent = Optional.empty();
        if(stepMethodSignature.isPresent()) {
            consumedEvent = stepMethodSignature.get().consumedEventName();
        }
        String domainProcessName = domainProcessDoc.name();
        Set<String> otherDomainProcesses = new HashSet<>();
        if(consumedEvent.isPresent()) {
            String moduleDocId = domainProcessDoc.moduleName();
            List<MessageListener> stepsProducingEvent = domain.findProducing(moduleDocId, consumedEvent.get());
            for(MessageListener stepDoc : stepsProducingEvent) {
                Set<String> processNames = stepDoc.processNames();
                for(String processName : processNames) {
                    if(!processName.equals(domainProcessName)) {
                        otherDomainProcesses.add(processName);
                    }
                }
            }
        }
        return otherDomainProcesses.stream()
                .map(DomainProcessGraphNodeName::new)
                .collect(toList());
    }

    private static Map<DomainProcessGraphNodeName, DomainProcessGraphNode> buildInterprocessSteps(
            String moduleName,
            Set<DomainProcessGraphNodeName> otherProcesses,
            Domain domain) {
        Map<DomainProcessGraphNodeName, DomainProcessGraphNode> interprocessSteps = new HashMap<>();
        List<DomainProcessGraphNodeName> otherProcessesList = new ArrayList<>(otherProcesses);
        Map<DomainProcessGraphNodeName, Set<String>> producedEventsPerProcess = new HashMap<>();
        Map<DomainProcessGraphNodeName, Set<String>> consumedEventsPerProcess = new HashMap<>();
        for(int i = 0; i < otherProcessesList.size(); ++i) {
            DomainProcessGraphNodeName processName1 = otherProcessesList.get(i);
            var process1 = domain.module(moduleName).orElseThrow().processes().stream()
                    .filter(process -> process.name().equals(processName1.stringValue()))
                    .findFirst().orElseThrow();
            Set<String> producedEventsOf1 = producedEventsPerProcess.computeIfAbsent(processName1, name -> producedEventsOfProcess(moduleName, name, domain));
            Set<String> consumedEventsOf1 = consumedEventsPerProcess.computeIfAbsent(processName1, name -> consumedEventsOfProcess(moduleName, name, domain));
            for(int j = i + 1; j < otherProcessesList.size(); ++j) {
                DomainProcessGraphNodeName processName2 = otherProcessesList.get(j);
                var process2 = domain.module(moduleName).orElseThrow().processes().stream()
                        .filter(process -> process.name().equals(processName2.stringValue()))
                        .findFirst().orElseThrow();
                Set<String> producedEventsOf2 = producedEventsPerProcess.computeIfAbsent(processName2, name -> producedEventsOfProcess(moduleName, name, domain));
                Set<String> consumedEventsOf2 = consumedEventsPerProcess.computeIfAbsent(processName2, name -> consumedEventsOfProcess(moduleName, name, domain));

                Set<String> producedBy1AndConsumedBy2 = intersect(producedEventsOf1, consumedEventsOf2);
                if(!producedBy1AndConsumedBy2.isEmpty()) {
                    interprocessSteps.put(processName1, new DomainProcessGraphNode.Builder()
                            .componentDoc(new DocumentationItem.Builder()
                                    .id(processName1 + "_" + processName2)
                                    .className(process1.className())
                                    .name(processName1.stringValue())
                                    .description(Documentation.empty())
                                    .moduleName(moduleName)
                                    .build())
                            .to(directStep(processName2))
                            .build());
                }

                Set<String> producedBy2AndConsumedBy1 = intersect(producedEventsOf2, consumedEventsOf1);
                if(!producedBy2AndConsumedBy1.isEmpty()) {
                    interprocessSteps.put(processName2, new DomainProcessGraphNode.Builder()
                            .componentDoc(new DocumentationItem.Builder()
                                    .id(processName2 + "_" + processName1)
                                    .className(process2.className())
                                    .name(processName2.stringValue())
                                    .description(Documentation.empty())
                                    .moduleName(moduleName)
                                    .build())
                            .to(directStep(processName1))
                            .build());
                }
            }
        }
        return interprocessSteps;
    }

    private static Set<String> producedEventsOfProcess(
            String moduleDocId,
            DomainProcessGraphNodeName processName,
            Domain domain) {
        Set<String> producedEvents = new HashSet<>();
        List<MessageListener> processStepDocs = domain.listeners(moduleDocId, processName.stringValue());
        for(MessageListener stepDoc : processStepDocs) {
            producedEvents.addAll(stepDoc.producedEvents().stream().map(NameRequired::name).collect(toList()));
        }
        return producedEvents;
    }

    private static Set<String> consumedEventsOfProcess(String moduleDocId, DomainProcessGraphNodeName processName, Domain domain) {
        Set<String> consumedEvents = new HashSet<>();
        List<MessageListener> processStepDocs = domain.listeners(moduleDocId, processName.stringValue());
        for(MessageListener stepDoc : processStepDocs) {
            Optional<StepMethodSignature> signature = stepDoc.stepMethodSignature();
            if(signature.isPresent()) {
                Optional<String> consumedEvent = signature.get().consumedEventName();
                if(consumedEvent.isPresent()) {
                    consumedEvents.add(consumedEvent.get());
                }
            }
        }
        return consumedEvents;
    }

    private static Set<String> intersect(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    private DomainProcessStepsFactory() {

    }
}