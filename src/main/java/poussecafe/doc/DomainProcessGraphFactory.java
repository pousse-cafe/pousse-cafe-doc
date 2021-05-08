package poussecafe.doc;

import java.util.Objects;
import java.util.Optional;
import poussecafe.doc.graph.DirectedEdge;
import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.Node;
import poussecafe.doc.graph.NodeStyle;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.DomainProcessSteps;
import poussecafe.doc.model.DomainProcessStepsFactory;
import poussecafe.doc.model.domainprocessdoc.Step;
import poussecafe.doc.model.domainprocessdoc.ToStep;

public class DomainProcessGraphFactory {

    public DirectedGraph buildGraph() {
        Logger.info("Building graph for process {}", domainProcessDoc.name());
        DirectedGraph graph = new DirectedGraph();
        DomainProcessSteps steps = domainProcessStepsFactory.buildDomainProcessSteps(domainProcessDoc, domain);
        for(Step step : steps.orderedSteps()) {
            if(step.external()) {
                Node node = Node.box(step.componentDoc().name());
                node.setStyle(Optional.of(NodeStyle.DASHED));
                graph.getNodesAndEdges().addNode(node);
            } else {
                graph.getNodesAndEdges().addNode(Node.ellipse(step.componentDoc().name()));
            }
            for(ToStep to : step.tos()) {
                Step stepTo = steps.getStep(to.name());
                DirectedEdge edge;
                if(to.directly()) {
                    edge = DirectedEdge.solidEdge(step.componentDoc().name(), to.name().stringValue());
                } else {
                    edge = DirectedEdge.dashedEdge(step.componentDoc().name(), to.name().stringValue());
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

    private DomainProcessStepsFactory domainProcessStepsFactory;

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

        public Builder domainProcessStepsFactory(DomainProcessStepsFactory domainProcessStepsFactory) {
            factory.domainProcessStepsFactory = domainProcessStepsFactory;
            return this;
        }

        public DomainProcessGraphFactory build() {
            Objects.requireNonNull(factory.domainProcessDoc);
            Objects.requireNonNull(factory.domain);
            Objects.requireNonNull(factory.domainProcessStepsFactory);
            return factory;
        }
    }

    private DomainProcessGraphFactory() {

    }
}
