package poussecafe.doc;

import java.util.Objects;
import java.util.Optional;
import poussecafe.doc.doclet.Logger;
import poussecafe.doc.graph.DirectedEdge;
import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.Node;
import poussecafe.doc.graph.NodeStyle;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.domainprocessdoc.DomainProcessGraphNode;
import poussecafe.doc.model.domainprocessdoc.ToStep;

public class DomainProcessGraphFactory {

    public DirectedGraph buildGraph() {
        Logger.info("Building graph for process {}", domainProcessDoc.name());
        var graph = new DirectedGraph();
        var nodes = DomainProcessStepsFactory.buildDomainProcessGraphNodes(domainProcessDoc, domain);
        for(DomainProcessGraphNode domainProcessGraphNode : nodes.orderedSteps()) {
            if(domainProcessGraphNode.external()) {
                var node = Node.box(domainProcessGraphNode.componentDoc().name());
                node.setStyle(Optional.of(NodeStyle.DASHED));
                graph.getNodesAndEdges().addNode(node);
            } else {
                graph.getNodesAndEdges().addNode(Node.ellipse(domainProcessGraphNode.componentDoc().name()));
            }
            for(ToStep to : domainProcessGraphNode.tos()) {
                DomainProcessGraphNode stepTo = nodes.getStep(to.name());
                DirectedEdge edge;
                if(to.directly()) {
                    edge = DirectedEdge.solidEdge(domainProcessGraphNode.componentDoc().name(), to.name().stringValue());
                } else {
                    edge = DirectedEdge.dashedEdge(domainProcessGraphNode.componentDoc().name(), to.name().stringValue());
                }

                Optional<String> consumedEvent = stepTo.consumedEvent();
                if(consumedEvent.isPresent()) {
                    edge.setLabel(consumedEvent.get());
                }

                graph.getNodesAndEdges().addEdge(edge);
            }
        }
        return graph;
    }

    private DocumentationItem domainProcessDoc;

    private Domain domain;

    public static class Builder {

        private DomainProcessGraphFactory factory = new DomainProcessGraphFactory();

        public Builder domainProcessDoc(DocumentationItem domainProcessDoc) {
            factory.domainProcessDoc = domainProcessDoc;
            return this;
        }

        public Builder domain(Domain domain) {
            factory.domain = domain;
            return this;
        }

        public DomainProcessGraphFactory build() {
            Objects.requireNonNull(factory.domainProcessDoc);
            Objects.requireNonNull(factory.domain);
            return factory;
        }
    }

    private DomainProcessGraphFactory() {

    }
}
