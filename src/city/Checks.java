package city;

import java.util.*;

public final class Checks {
    private static int count;
    private static void verify(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
        count++; System.out.println("PASS " + message);
    }
    public static void run() {
        count = 0;
        Graph g = new Graph(4, false);
        g.add(0,1,1); g.add(1,2,2); g.add(2,3,3); g.add(0,3,12); g.add(0,2,8);
        verify(SpanningTrees.kruskal(g).weight() == 6, "Kruskal weight");
        verify(SpanningTrees.prim(g).weight() == 6, "Prim weight");
        verify(SpanningTrees.kruskal(g).connected(), "MST connected");
        Graph disconnected = new Graph(4, false); disconnected.add(0, 1, 2);
        verify(!SpanningTrees.kruskal(disconnected).connected(), "Kruskal disconnected");
        verify(!SpanningTrees.prim(disconnected).connected(), "Prim disconnected");
        Graph one = new Graph(1, false);
        verify(SpanningTrees.kruskal(one).connected() && SpanningTrees.prim(one).connected(), "Single vertex");
        Graph equal = new Graph(3, false); equal.add(0,1,2); equal.add(1,2,2); equal.add(0,2,2);
        verify(SpanningTrees.kruskal(equal).weight() == 4 && SpanningTrees.prim(equal).weight() == 4, "Duplicate weights multiple MSTs");
        DisjointSet ds = new DisjointSet(3);
        verify(ds.union(0,1) && ds.find(0)==ds.find(1) && ds.components()==2, "DSU union and find");
        Graph dag = new Graph(4, true); dag.add(0,1,1); dag.add(0,2,2); dag.add(1,3,1); dag.add(2,3,1);
        Connectivity.TopoResult t = Connectivity.kahn(dag);
        verify(t.dag() && t.order().size()==4, "Kahn DAG ordering");
        int[] position = new int[4]; for(int i=0;i<4;i++)position[t.order().get(i)]=i;
        verify(dag.edges().stream().allMatch(e -> position[e.from()] < position[e.to()]), "Valid topological edge order");
        Graph cyclic = new Graph(4, true); cyclic.add(0,1,1); cyclic.add(1,0,1); cyclic.add(1,2,1); cyclic.add(2,3,1);
        verify(!Connectivity.kahn(cyclic).dag(), "Cycle detection");
        List<List<Integer>> groups = Connectivity.tarjan(cyclic).groups();
        verify(groups.size()==3 && groups.stream().anyMatch(s -> s.equals(List.of(0,1))), "Tarjan SCC groups");
        Set<Integer> union = new HashSet<>(); for(List<Integer> group : groups) union.addAll(group);
        verify(union.size()==cyclic.size() && groups.stream().mapToInt(List::size).sum()==cyclic.size(), "Every vertex in exactly one SCC");
        Routes.Result r = Routes.dijkstra(dag,0);
        verify(r.distance()[3]==2 && r.path(3).equals(List.of(0,1,3)), "Dijkstra shortest path");
        Graph inaccessible = new Graph(3, true); inaccessible.add(0,1,1);
        verify(Routes.dijkstra(inaccessible,0).distance()[2]==Routes.INF && Routes.dijkstra(inaccessible,0).path(2).isEmpty(), "Unreachable vertex");
        boolean reject = false;
        try { inaccessible.add(0,2,-1); } catch (IllegalArgumentException ex) { reject = true; }
        verify(reject, "Negative weights rejected");
        Graph rnd = CityGenerator.roads(10,30,94721);
        verify(SpanningTrees.kruskal(rnd).weight()==SpanningTrees.prim(rnd).weight(), "Generated MST agreement");
        verify(Connectivity.kahn(CityGenerator.directed(10,30,94721,true)).dag(), "Generated DAG");
        System.out.println("PASS " + count + " correctness checks\n");
    }
}
