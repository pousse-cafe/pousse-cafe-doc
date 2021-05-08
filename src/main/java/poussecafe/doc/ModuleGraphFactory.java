package poussecafe.doc;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import poussecafe.doc.graph.Node;
import poussecafe.doc.graph.NodeStyle;
import poussecafe.doc.graph.UndirectedEdge;
import poussecafe.doc.graph.UndirectedGraph;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.Module;
import poussecafe.doc.model.Relation;
import poussecafe.doc.model.relationdoc.ComponentType;
import poussecafe.source.analysis.ClassName;

import static java.util.stream.Collectors.toList;

public class ModuleGraphFactory {

    public UndirectedGraph buildGraph() {
        addSimpleAggregates();
        return graph;
    }

    private Module moduleDoc;

    private UndirectedGraph graph = new UndirectedGraph();

    private void addSimpleAggregates() {
        for (Aggregate aggregateDoc : moduleDoc.aggregates()) {
            addSimpleAggregate(aggregateDoc);
            addAggregateRelations(aggregateDoc);
        }
    }

    private Domain domain;

    private void addSimpleAggregate(Aggregate aggregateDoc) {
        Node node = Node.box(aggregateDoc.documentation().name());
        node.setStyle(Optional.of(NodeStyle.BOLD));
        graph.getNodesAndEdges().addNode(node);
    }

    private void addAggregateRelations(Aggregate aggregateDoc) {
        addAggregateRelations(aggregateDoc, aggregateDoc.documentation().className().orElseThrow(), new ExplorationState());
    }

    private class ExplorationState {

        Set<ClassName> exploredComponends = new HashSet<>();
    }

    private void addAggregateRelations(Aggregate aggregateDoc, ClassName fromClassName, ExplorationState explorationState) {
        explorationState.exploredComponends.add(fromClassName);
        for(Relation relation : findRelationsWithFromClassName(fromClassName)) {
            if(relation.to().type() == ComponentType.AGGREGATE) {
                Aggregate otherAggregate = aggregateWithClass(relation.to().className());
                if(!aggregateDoc.documentation().id().equals(otherAggregate.documentation().id())) {
                    UndirectedEdge edge = UndirectedEdge
                            .solidEdge(aggregateDoc.documentation().name(), relation.to().name());
                    graph.getNodesAndEdges().addEdge(edge);
                }
            } else if(!explorationState.exploredComponends.contains(relation.to().className())) {
                addAggregateRelations(aggregateDoc, relation.to().className(), explorationState);
            }
        }
    }

    private List<Relation> findRelationsWithFromClassName(ClassName fromId) {
        return domain.relations().stream()
                .filter(item -> item.from().className().equals(fromId))
                .collect(toList());
    }

    private Aggregate aggregateWithClass(ClassName className) {
        return moduleDoc.aggregates().stream()
                .filter(item -> item.documentation().className().equals(Optional.of(className)))
                .findFirst().orElseThrow();
    }

    public static class Builder {

        private ModuleGraphFactory factory = new ModuleGraphFactory();

        public Builder moduleDoc(Module moduleDoc) {
            factory.moduleDoc = moduleDoc;
            return this;
        }

        public Builder domain(Domain domain) {
            factory.domain = domain;
            return this;
        }

        public ModuleGraphFactory build() {
            Objects.requireNonNull(factory.moduleDoc);
            Objects.requireNonNull(factory.domain);
            return factory;
        }
    }

    private ModuleGraphFactory() {

    }
}
