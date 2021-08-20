package poussecafe.doc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import poussecafe.doc.doclet.Logger;
import poussecafe.doc.graph.DirectedEdge;
import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.Node;
import poussecafe.doc.graph.NodeStyle;
import poussecafe.doc.graph.NodesAndEdges;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.MessageListener;
import poussecafe.doc.model.processstepdoc.NameRequired;
import poussecafe.doc.model.processstepdoc.StepMethodSignature;

import static java.util.stream.Collectors.toList;

public class AggregateEventsGraphFactory {

    public static DirectedGraph buildGraph(
            Aggregate aggregate,
            Domain domain) {
        DocumentationItem aggregateDoc = aggregate.documentation();
        String moduleName = aggregateDoc.moduleName();
        Logger.info("Building events graph for aggregate {}", aggregateDoc.name());
        DirectedGraph graph = new DirectedGraph();
        NodesAndEdges nodesAndEdges = graph.getNodesAndEdges();

        String aggregateName = aggregateDoc.name();
        Node aggregateNode = Node.box(aggregateName);
        aggregateNode.setStyle(Optional.of(NodeStyle.BOLD));
        nodesAndEdges.addNode(aggregateNode);

        List<MessageListener> aggregateSteps = domain.listeners(moduleName)
                .filter(item -> item.aggregate().isPresent())
                .filter(item -> item.aggregate().orElseThrow().equals(aggregateName))
                .collect(toList());
        for(MessageListener stepDoc : aggregateSteps) {
            StepMethodSignature signature = stepDoc.stepMethodSignature().orElseThrow();
            Optional<String> optionalConsumedEvent = signature.consumedEventName();
            if(optionalConsumedEvent.isPresent()) {
                String consumedEvent = optionalConsumedEvent.get();

                Node eventNode = Node.ellipse(consumedEvent);
                nodesAndEdges.addNode(eventNode);
                nodesAndEdges.addEdge(DirectedEdge.solidEdge(consumedEvent, aggregateName));

                List<MessageListener> fromSteps = domain.findProducing(moduleName, consumedEvent);
                for(MessageListener fromStep : fromSteps) {
                    Optional<String> optionalAggregateDocId = fromStep.aggregate();
                    if(optionalAggregateDocId.isPresent()) {
                        Node fromExternalNode = Node.box(optionalAggregateDocId.orElseThrow());
                        nodesAndEdges.addNode(fromExternalNode);
                        nodesAndEdges.addEdge(DirectedEdge.solidEdge(optionalAggregateDocId.orElseThrow(), consumedEvent));
                    }
                }

                List<String> fromExternals = stepDoc.fromExternals();
                for(String fromExternal : fromExternals) {
                    Node fromExternalNode = Node.box(fromExternal);
                    fromExternalNode.setStyle(Optional.of(NodeStyle.DASHED));
                    nodesAndEdges.addNode(fromExternalNode);
                    nodesAndEdges.addEdge(DirectedEdge.solidEdge(fromExternal, consumedEvent));
                }

                for(NameRequired producedEvent : stepDoc.producedEvents()) {
                    Node producedEventNode = Node.ellipse(producedEvent.name());
                    nodesAndEdges.addNode(producedEventNode);
                    if(producedEvent.required()) {
                        nodesAndEdges.addEdge(DirectedEdge.solidEdge(aggregateName, producedEvent.name()));
                    } else {
                        nodesAndEdges.addEdge(DirectedEdge.dashedEdge(aggregateName, producedEvent.name()));
                    }

                    List<MessageListener> toSteps = domain.findConsuming(moduleName, producedEvent.name());
                    for(MessageListener toStep : toSteps) {
                        Optional<String> optionalAggregateDocId = toStep.aggregate();
                        if(optionalAggregateDocId.isPresent()) {
                            Node fromExternalNode = Node.box(optionalAggregateDocId.orElseThrow());
                            nodesAndEdges.addNode(fromExternalNode);
                            nodesAndEdges.addEdge(DirectedEdge.solidEdge(producedEvent.name(), optionalAggregateDocId.orElseThrow()));
                        }
                    }
                }

                List<String> toExternals = stepDoc.toExternals();
                for(String toExternal : toExternals) {
                    Node toExternalNode = Node.box(toExternal);
                    toExternalNode.setStyle(Optional.of(NodeStyle.DASHED));
                    nodesAndEdges.addNode(toExternalNode);
                    nodesAndEdges.addEdge(DirectedEdge.solidEdge(aggregateName, toExternal));
                }

                Map<NameRequired, List<String>> toExternalsByEvent = stepDoc.toExternalsByEvent();
                for(Entry<NameRequired, List<String>> toExternal : toExternalsByEvent.entrySet()) {
                    NameRequired eventName = toExternal.getKey();
                    List<String> externalNames = toExternal.getValue();

                    Node toEventNode = Node.ellipse(eventName.name());
                    nodesAndEdges.addNode(toEventNode);
                    if(eventName.required()) {
                        nodesAndEdges.addEdge(DirectedEdge.solidEdge(aggregateName, eventName.name()));
                    } else {
                        nodesAndEdges.addEdge(DirectedEdge.dashedEdge(aggregateName, eventName.name()));
                    }

                    for(String externalName : externalNames) {
                        Node toExternalNode = Node.box(externalName);
                        toExternalNode.setStyle(Optional.of(NodeStyle.DASHED));
                        nodesAndEdges.addNode(toExternalNode);
                        nodesAndEdges.addEdge(DirectedEdge.solidEdge(eventName.name(), externalName));
                    }
                }
            }
        }

        return graph;
    }

    private AggregateEventsGraphFactory() {

    }
}
