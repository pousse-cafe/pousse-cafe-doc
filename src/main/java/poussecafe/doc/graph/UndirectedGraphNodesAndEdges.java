package poussecafe.doc.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UndirectedGraphNodesAndEdges implements NodesAndEdges {

    private Map<String, Node> nodes = new HashMap<>();

    private Set<UndirectedEdge> edges = new HashSet<>();

    @Override
    public void addEdge(Edge edge) {
        edges.add((UndirectedEdge) edge);
    }

    public List<UndirectedEdge> edges() {
        return new ArrayList<>(edges);
    }

    public List<Node> nodes() {
        return new ArrayList<>(nodes.values());
    }

    @Override
    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public Node getNode(String name) {
        return nodes.get(name);
    }
}
