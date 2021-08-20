package poussecafe.doc;

import poussecafe.doc.graph.DirectedGraph;
import poussecafe.doc.graph.UndirectedGraph;
import poussecafe.doc.model.Aggregate;
import poussecafe.doc.model.DocumentationItem;
import poussecafe.doc.model.Domain;
import poussecafe.doc.model.Module;

public class GraphFactory {

    public static UndirectedGraph buildModuleGraph(
            Module moduleDoc,
            Domain domain) {
        return new ModuleGraphFactory.Builder()
                .moduleDoc(moduleDoc)
                .domain(domain)
                .build()
                .buildGraph();
    }

    public static UndirectedGraph buildAggregateGraph(
            Aggregate aggregateDoc,
            Domain domain) {
        return new AggregateGraphFactory.Builder()
                .aggregateDoc(aggregateDoc)
                .domain(domain)
                .build()
                .buildGraph();
    }

    public static DirectedGraph buildDomainProcessGraph(DocumentationItem domainProcessDoc, Domain domain) {
        return new DomainProcessGraphFactory.Builder()
                .domainProcessDoc(domainProcessDoc)
                .domain(domain)
                .build()
                .buildGraph();
    }

    private GraphFactory() {

    }
}
