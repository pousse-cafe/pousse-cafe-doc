package poussecafe.doc;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import poussecafe.doc.doclet.Logger;
import poussecafe.doc.graph.Node;
import poussecafe.doc.graph.NodeStyle;
import poussecafe.doc.graph.UndirectedEdge;
import poussecafe.doc.graph.UndirectedGraph;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.Module;
import poussecafe.doc.model.Relation;
import poussecafe.doc.model.relationdoc.Component;
import poussecafe.doc.model.relationdoc.ComponentType;
import poussecafe.source.analysis.ClassName;

import static java.util.stream.Collectors.toList;

public class AggregateGraphFactory {

    private Set<ClassName> exploredFromClassNames = new HashSet<>();

    public UndirectedGraph buildGraph() {
        String aggregateNodeName = addAggregate();
        AggregateGraphPath path = new AggregateGraphPath().with(aggregateNodeName);
        addAllRelations(path, aggregateDoc.documentation().className().orElseThrow());
        return graph;
    }

    private String addAggregate() {
        Logger.debug("Aggregate " + aggregateDoc.documentation().id());
        String nodeName = aggregateDoc.documentation().name();
        Node node = Node.box(nodeName);
        node.setStyle(Optional.of(NodeStyle.BOLD));
        graph.getNodesAndEdges().addNode(node);
        return nodeName;
    }

    private Aggregate aggregateDoc;

    private UndirectedGraph graph = new UndirectedGraph();

    private void addAllRelations(AggregateGraphPath path, ClassName fromClassName) {
        if(!exploredFromClassNames.contains(fromClassName)) {
            exploredFromClassNames.add(fromClassName);

            for(Relation relation : findWithFromClassName(fromClassName)) {
                Component toComponent = relation.to();

                Logger.debug("Relation " + fromClassName + " -> " + toComponent.className());
                if(toComponent.type() != ComponentType.AGGREGATE) {
                    String newNodeName = toComponent.name();
                    AggregateGraphPath newPath = path.with(newNodeName);
                    addNonAggregateRelation(path, toComponent, newNodeName);
                    addAllRelations(newPath, relation.to().className());
                } else {
                    addAggregateRelation(path, toComponent);
                }
            }
        }
    }

    private List<Relation> findWithFromClassName(ClassName fromClassName) {
        return domain.relations().stream()
                .filter(item -> item.from().className().equals(fromClassName))
                .collect(toList());
    }

    private Domain domain;

    private void addAggregateRelation(AggregateGraphPath path, Component toComponent) {
        var aggregateClassName = aggregateDoc.documentation().className().orElseThrow();
        if(toComponent.className().equals(aggregateClassName)) {
            return;
        }

        var toAggregateDoc = module.aggregate(toComponent.name());
        if(toAggregateDoc.isPresent()) {
            String toName = toComponent.name();
            addNode(toComponent, toName);
            UndirectedEdge edge = UndirectedEdge.dashedEdge(path.lastName(), toName);
            graph.getNodesAndEdges().addEdge(edge);
        }
    }

    private Module module;

    private String addNonAggregateRelation(AggregateGraphPath path, Component toComponent, String toName) {
        addNode(toComponent, toName);
        UndirectedEdge edge = UndirectedEdge.solidEdge(path.lastName(), toName);
        graph.getNodesAndEdges().addEdge(edge);
        return toName;
    }

    private void addNode(Component component, String candidateName) {
        if(graph.getNodesAndEdges().getNode(candidateName) == null) {
            Node node = node(component, candidateName);
            graph.getNodesAndEdges().addNode(node);
        }
    }

    private Node node(Component component,
            String name) {
        if(component.type() == ComponentType.ENTITY) {
            return Node.box(name);
        } else if(component.type() == ComponentType.VALUE_OBJECT) {
            return Node.ellipse(name);
        } else if(component.type() == ComponentType.AGGREGATE) {
            Node node = Node.box(name);
            node.setStyle(Optional.of(NodeStyle.BOLD));
            return node;
        } else {
            throw new IllegalArgumentException("Unsupported component type " + component.type());
        }
    }

    public static class Builder {

        private AggregateGraphFactory factory = new AggregateGraphFactory();

        public Builder aggregateDoc(Aggregate aggregateDoc) {
            factory.aggregateDoc = aggregateDoc;
            return this;
        }

        public Builder domain(Domain domain) {
            factory.domain = domain;
            return this;
        }

        public AggregateGraphFactory build() {
            Objects.requireNonNull(factory.aggregateDoc);
            Objects.requireNonNull(factory.domain);

            factory.module = factory.domain.module(factory.aggregateDoc.documentation().moduleName()).orElseThrow();

            return factory;
        }
    }

    private AggregateGraphFactory() {

    }
}
