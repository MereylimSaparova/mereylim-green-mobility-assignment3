package city;

public final class DisjointSet {
    private final int[] parent, size;
    private int components;
    public long operations;
    public DisjointSet(int n) {
        parent = new int[n]; size = new int[n]; components = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }
    public int find(int x) {
        operations++;
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    public boolean union(int a, int b) {
        operations++;
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        size[ra] += size[rb];
        components--;
        return true;
    }
    public int components() { return components; }
}
