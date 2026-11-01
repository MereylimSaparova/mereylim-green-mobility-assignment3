package city;

import java.util.*;

public final class Graph {
    public record Edge(int from, int to, int weight) {}
    private final List<List<Edge>> adj;
    private final List<Edge> edges = new ArrayList<>();
    private final boolean directed;
    public Graph(int vertices, boolean directed) {
        if (vertices < 1) throw new IllegalArgumentException("At least one vertex required");
        this.directed = directed;
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++) adj.add(new ArrayList<>());
    }
    public void add(int from, int to, int weight) {
        if (from < 0 || to < 0 || from >= size() || to >= size()) throw new IllegalArgumentException("Invalid vertex");
        if (weight < 0) throw new IllegalArgumentException("Dijkstra needs nonnegative weights");
        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adj.get(from).add(edge);
        if (!directed) adj.get(to).add(new Edge(to, from, weight));
    }
    public int size() { return adj.size(); }
    public boolean directed() { return directed; }
    public List<Edge> edges() { return Collections.unmodifiableList(edges); }
    public List<Edge> neighbors(int v) { return Collections.unmodifiableList(adj.get(v)); }
}
