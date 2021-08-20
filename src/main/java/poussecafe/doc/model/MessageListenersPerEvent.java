package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNodeName;
import poussecafe.doc.model.processstepdoc.NameRequired;

import static java.util.stream.Collectors.toList;

public class MessageListenersPerEvent {

    public static class Builder {

        private MessageListenersPerEvent listenersPerEvent = new MessageListenersPerEvent();

        public Builder withMessageListener(MessageListener processStepDoc) {
            Optional<String> consumedEventName;
            var optionalStepMethodSignature = processStepDoc.stepMethodSignature();
            if(optionalStepMethodSignature.isPresent()) {
                consumedEventName = optionalStepMethodSignature.get().consumedEventName();
            } else {
                consumedEventName = Optional.empty();
            }

            if(consumedEventName.isPresent()) {
                String presentConsumedEventName = consumedEventName.get();
                var listeners = listenersPerEvent.eventToListenersMap.computeIfAbsent(presentConsumedEventName,
                        key -> new ArrayList<>());
                listeners.add(processStepDoc.documentation().name());
            }
            return this;
        }

        public MessageListenersPerEvent build() {
            return listenersPerEvent;
        }
    }

    private HashMap<String, List<String>> eventToListenersMap = new HashMap<>();

    public List<DomainProcessGraphNodeName> locateToInternals(MessageListener stepDoc) {
        List<DomainProcessGraphNodeName> tos = new ArrayList<>();
        for(NameRequired producedEvent : stepDoc.producedEvents()) {
            List<String> consumingSteps = eventToListenersMap.get(producedEvent.name());
            if(consumingSteps != null) {
                tos.addAll(consumingSteps.stream().map(DomainProcessGraphNodeName::new).collect(toList()));
            }
        }
        return tos;
    }
}
