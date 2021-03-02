package poussecafe.doc.graph;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UndirectedSubGraph implements SubGraph {

    private List<UndirectedSubGraph> subGraphs = new ArrayList<>();

    private UndirectedGraphNodesAndEdges nodesAndEdges = new UndirectedGraphNodesAndEdges();

    private String name;

    @Override
    public List<SubGraph> getSubGraphs() {
        return new ArrayList<>(subGraphs);
    }

    public void addSubGraph(UndirectedSubGraph subGraph) {
        subGraphs.add(subGraph);
    }

    public UndirectedGraphNodesAndEdges getNodesAndEdges() {
        return nodesAndEdges;
    }

    public void setNodesAndEdges(UndirectedGraphNodesAndEdges nodesAndEdges) {
        this.nodesAndEdges = nodesAndEdges;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasName() {
        return !StringUtils.isEmpty(name);
    }

}