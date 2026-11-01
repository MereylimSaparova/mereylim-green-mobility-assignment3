package city;

import java.util.*;

public final class Connectivity {
    public record TopoResult(List<Integer> order, boolean dag, long operations) {}
    public record SccResult(List<List<Integer>> groups, long operations) {}
    private Connectivity() {}
    private static void directed(Graph g) {
        if (!g.directed()) throw new IllegalArgumentException("Directed graph required");
    }
    public static TopoResult kahn(Graph g) {
        directed(g);
        int n = g.size(); int[] in = new int[n]; long operations = 0;
        for (Graph.Edge e : g.edges()) { in[e.to()]++; operations++; }
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (in[i] == 0) q.add(i);
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int v = q.remove(); order.add(v); operations++;
            for (Graph.Edge e : g.neighbors(v)) { operations++; if (--in[e.to()] == 0) q.add(e.to()); }
        }
        return new TopoResult(order, order.size() == n, operations);
    }
    public static SccResult tarjan(Graph g) {
        directed(g);
        return new Tarjan(g).run();
    }
    private static final class Tarjan {
        final Graph g;
        final int[] index, low;
        final boolean[] onStack;
        final Deque<Integer> stack = new ArrayDeque<>();
        final List<List<Integer>> groups = new ArrayList<>();
        int clock;
        long operations;
        Tarjan(Graph g) {
            this.g = g;
            index = new int[g.size()]; low = new int[g.size()]; onStack = new boolean[g.size()];
            Arrays.fill(index, -1);
        }
        SccResult run() {
            for (int v = 0; v < g.size(); v++) if (index[v] == -1) dfs(v);
            return new SccResult(groups, operations);
        }
        void dfs(int v) {
            index[v] = low[v] = clock++;
            stack.push(v); onStack[v] = true; operations++;
            for (Graph.Edge e : g.neighbors(v)) {
                operations++;
                int w = e.to();
                if (index[w] == -1) { dfs(w); low[v] = Math.min(low[v], low[w]); }
                else if (onStack[w]) low[v] = Math.min(low[v], index[w]);
            }
            if (low[v] == index[v]) {
                List<Integer> group = new ArrayList<>(); int w;
                do { w = stack.pop(); onStack[w] = false; group.add(w); operations++; } while (w != v);
                Collections.sort(group); groups.add(group);
            }
        }
    }
}
