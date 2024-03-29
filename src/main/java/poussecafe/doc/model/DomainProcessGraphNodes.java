package poussecafe.doc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNode;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNodeName;
import poussecafe.doc.model.domainprocessdoc.ToStep;

import static java.util.stream.Collectors.toList;

public class DomainProcessGraphNodes {

    private Map<DomainProcessGraphNodeName, DomainProcessGraphNode> nodes;

    public Map<DomainProcessGraphNodeName, DomainProcessGraphNode> steps() {
        return nodes;
    }

    public List<DomainProcessGraphNode> orderedSteps() {
        if(orderedSteps == null) {
            Map<String, List<String>> graph = buildGraphMap();
            List<String> orderedStepNames = topologicalOrdering(graph);
            orderedSteps = orderedStepNames
                    .stream()
                    .map(DomainProcessGraphNodeName::new)
                    .map(stepName -> nodes.get(stepName))
                    .collect(toList());
        }
        return orderedSteps;
    }

    private List<DomainProcessGraphNode> orderedSteps;

    private Map<String, List<String>> buildGraphMap() {
        Map<String, List<String>> graph = new HashMap<>();
        for(DomainProcessGraphNode step : nodes.values()) {
            if(!graph.containsKey(step.componentDoc().name())) {
                graph.put(step.componentDoc().name(), new ArrayList<>());
            }
            for(ToStep to : step.tos()) {
                List<String> froms = graph.get(to.name().stringValue());
                if(froms == null) {
                    froms = new ArrayList<>();
                    graph.put(to.name().stringValue(), froms);
                }
                froms.add(step.componentDoc().name());
            }
        }
        return graph;
    }

    private List<String> topologicalOrdering(Map<String, List<String>> graph) {
        List<String> orderedNodes = new ArrayList<>();
        Map<String, List<String>> partialGraph = new HashMap<>(graph);
        while(partialGraph.size() > 0) {
            String nodeWithoutFrom = findNodeWithoutFrom(partialGraph);
            orderedNodes.add(nodeWithoutFrom);
            removeNode(partialGraph, nodeWithoutFrom);
        }
        return orderedNodes;
    }

    private String findNodeWithoutFrom(Map<String, List<String>> graph) {
        for(Entry<String, List<String>> e : graph.entrySet()) {
            if(e.getValue().isEmpty()) {
                return e.getKey();
            }
        }
        return graph.entrySet().iterator().next().getKey();
    }

    private void removeNode(Map<String, List<String>> graph, String nodeName) {
        Iterator<Entry<String, List<String>>> iterator = graph.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, List<String>> entry = iterator.next();
            if(entry.getKey().equals(nodeName)) {
                iterator.remove();
            } else {
                entry.getValue().remove(nodeName);
            }
        }
    }

    public DomainProcessGraphNode getStep(DomainProcessGraphNodeName name) {
        return nodes.get(name);
    }

    public static class Builder {

        private Map<DomainProcessGraphNodeName, DomainProcessGraphNode> steps = new HashMap<>();

        public Builder merge(Map<DomainProcessGraphNodeName, DomainProcessGraphNode> stepsToBeMerged) {
            for(Entry<DomainProcessGraphNodeName, DomainProcessGraphNode> entry : stepsToBeMerged.entrySet()) {
                DomainProcessGraphNode existingStep = steps.get(entry.getKey());
                if(existingStep == null) {
                    steps.put(entry.getKey(), entry.getValue());
                } else {
                    steps.put(entry.getKey(), new DomainProcessGraphNode.Builder()
                            .step(existingStep)
                            .tos(entry.getValue().tos())
                            .build());
                }
            }
            return this;
        }

        public Builder add(DomainProcessGraphNode step) {
            steps.put(step.stepName(), step);
            return this;
        }

        public DomainProcessGraphNode getStep(DomainProcessGraphNodeName stepName) {
            return steps.get(stepName);
        }

        public DomainProcessGraphNodes build() {
            return new DomainProcessGraphNodes(steps);
        }
    }

    private DomainProcessGraphNodes(Map<DomainProcessGraphNodeName, DomainProcessGraphNode> steps) {
        Objects.requireNonNull(steps);
        this.nodes = steps;
    }
}
