package city;

import java.util.*;

public final class Routes {
    public static final long INF = Long.MAX_VALUE / 4;
    public record Result(long[] distance, int[] parent, long operations) {
        public List<Integer> path(int target) {
            if (distance[target] == INF) return List.of();
            LinkedList<Integer> path = new LinkedList<>();
            for (int v = target; v != -1; v = parent[v]) path.addFirst(v);
            return path;
        }
    }
    private record State(int vertex, long distance) {}
    private Routes() {}
    public static Result dijkstra(Graph g, int source) {
        if (source < 0 || source >= g.size()) throw new IllegalArgumentException("Invalid source");
        long[] dist = new long[g.size()]; int[] parent = new int[g.size()];
        Arrays.fill(dist, INF); Arrays.fill(parent, -1);
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingLong(State::distance));
        dist[source] = 0; pq.add(new State(source, 0));
        long operations = 0;
        while (!pq.isEmpty()) {
            State cur = pq.remove(); operations++;
            if (cur.distance() != dist[cur.vertex()]) continue;
            for (Graph.Edge e : g.neighbors(cur.vertex())) {
                operations++;
                if (e.weight() < 0) throw new IllegalArgumentException("Negative edge");
                long candidate = cur.distance() + e.weight();
                if (candidate < dist[e.to()]) {
                    dist[e.to()] = candidate; parent[e.to()] = cur.vertex();
                    pq.add(new State(e.to(), candidate));
                }
            }
        }
        return new Result(dist, parent, operations);
    }
}
