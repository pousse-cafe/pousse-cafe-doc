package poussecafe.doc.model;

import poussecafe.doc.AggregateGraphFactory;
import poussecafe.doc.DocumentationItem;
import poussecafe.doc.DomainProcessGraphFactory;
import poussecafe.doc.ModuleGraphFactory;
import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.UndirectedGraph;
import poussecafe.domain.Service;

public class GraphFactory implements Service {

    public UndirectedGraph buildModuleGraph(
            Module moduleDoc,
            Domain domain) {
        return new ModuleGraphFactory.Builder()
                .moduleDoc(moduleDoc)
                .domain(domain)
                .build()
                .buildGraph();
    }

    public UndirectedGraph buildAggregateGraph(
            Aggregate aggregateDoc,
            Domain domain) {
        return new AggregateGraphFactory.Builder()
                .aggregateDoc(aggregateDoc)
                .domain(domain)
                .build()
                .buildGraph();
    }

    public DirectedGraph buildDomainProcessGraph(DocumentationItem domainProcessDoc, Domain domain) {
        return new DomainProcessGraphFactory.Builder()
                .domainProcessDoc(domainProcessDoc)
                .domainProcessStepsFactory(domainProcessStepsFactory)
                .domain(domain)
                .build()
                .buildGraph();
    }

    private DomainProcessStepsFactory domainProcessStepsFactory;
}
