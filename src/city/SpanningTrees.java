package city;

import java.util.*;

public final class SpanningTrees {
    public record Result(long weight, boolean connected, int edgeCount, long operations) {}
    private SpanningTrees() {}
    private static void check(Graph g) {
        if (g.directed()) throw new IllegalArgumentException("MST requires undirected graph");
    }
    public static Result kruskal(Graph g) {
        check(g);
        List<Graph.Edge> sorted = new ArrayList<>(g.edges());
        sorted.sort(Comparator.comparingInt(Graph.Edge::weight));
        DisjointSet ds = new DisjointSet(g.size());
        long weight = 0, examined = 0;
        int chosen = 0;
        for (Graph.Edge e : sorted) {
            examined++;
            if (ds.union(e.from(), e.to())) { weight += e.weight(); chosen++; }
            if (chosen == g.size() - 1) break;
        }
        return new Result(weight, chosen == g.size() - 1, chosen, examined + ds.operations);
    }
    public static Result prim(Graph g) {
        check(g);
        boolean[] visited = new boolean[g.size()];
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Graph.Edge::weight));
        visited[0] = true;
        pq.addAll(g.neighbors(0));
        long weight = 0, examined = 0;
        int chosen = 0;
        while (!pq.isEmpty()) {
            Graph.Edge e = pq.poll(); examined++;
            if (visited[e.to()]) continue;
            visited[e.to()] = true;
            chosen++; weight += e.weight();
            for (Graph.Edge next : g.neighbors(e.to())) { examined++; if (!visited[next.to()]) pq.add(next); }
        }
        return new Result(weight, chosen == g.size() - 1, chosen, examined);
    }
}
