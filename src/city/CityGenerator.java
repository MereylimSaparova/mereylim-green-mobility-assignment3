package city;

import java.util.*;

public final class CityGenerator {
    private CityGenerator() {}
    private static long key(int u, int v, boolean directed) {
        if (!directed && u > v) { int t = u; u = v; v = t; }
        return ((long) u << 32) | (v & 0xffffffffL);
    }
    public static Graph roads(int vertices, int edges, long seed) {
        if (edges < vertices - 1 || edges > (long)vertices * (vertices - 1) / 2)
            throw new IllegalArgumentException("Invalid undirected edge count");
        Random random = new Random(seed);
        Graph g = new Graph(vertices, false); Set<Long> seen = new HashSet<>();
        for (int v = 1; v < vertices; v++) {
            int u = random.nextInt(v);
            seen.add(key(u, v, false)); g.add(u, v, 1 + random.nextInt(90));
        }
        while (g.edges().size() < edges) {
            int u = random.nextInt(vertices), v = random.nextInt(vertices);
            if (u != v && seen.add(key(u, v, false))) g.add(u, v, 1 + random.nextInt(90));
        }
        return g;
    }
    public static Graph directed(int vertices, int edges, long seed, boolean dag) {
        long max = dag ? (long)vertices * (vertices - 1) / 2 : (long)vertices * (vertices - 1);
        if (edges > max || edges < 0) throw new IllegalArgumentException("Invalid directed edge count");
        Random random = new Random(seed);
        Graph g = new Graph(vertices, true); Set<Long> seen = new HashSet<>();
        while (g.edges().size() < edges) {
            int u = random.nextInt(vertices), v = random.nextInt(vertices);
            if (dag && u > v) { int t = u; u = v; v = t; }
            if (u != v && seen.add(key(u, v, true))) g.add(u, v, 1 + random.nextInt(90));
        }
        return g;
    }
}
