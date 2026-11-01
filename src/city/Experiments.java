package city;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.LongSupplier;

public final class Experiments {
    private static volatile long blackHole;
    private static final int[] V = {10, 100, 1000};
    private static final int[] E = {30, 500, 5000};
    private static final long SEED = 94721L;
    private record Run(long ns, long operations) {}
    private static Run measure(LongSupplier computation, LongSupplier operations) {
        long start = System.nanoTime();
        blackHole = computation.getAsLong();
        long elapsed = System.nanoTime() - start;
        return new Run(elapsed, operations.getAsLong());
    }
    private static String row(String algorithm, int v, int e, long[] times, long[] ops) {
        double avgT = Arrays.stream(times).average().orElse(0);
        double avgO = Arrays.stream(ops).average().orElse(0);
        return String.format(Locale.US, "%s,%d,%d,%d,%.2f,%.2f", algorithm,v,e,5,avgT,avgO);
    }
    public static void run() throws IOException {
        Path table = Path.of("results/tables/benchmarks.csv");
        Files.createDirectories(table.getParent());
        List<String> lines = new ArrayList<>();
        lines.add("algorithm,vertices,edges,repetitions,average_time_ns,average_operations");
        for(int k=0;k<V.length;k++) {
            int v = V[k], e = E[k];
            Graph undirected = CityGenerator.roads(v,e,SEED+k);
            Graph directed = CityGenerator.directed(v,e,SEED+100+k,false);
            Graph dag = CityGenerator.directed(v,e,SEED+200+k,true);
            for (String name : List.of("Kruskal", "Prim", "Tarjan_SCC", "Kahn_DAG", "Kahn_Cyclic", "Dijkstra")) {
                long[] times = new long[5], ops = new long[5];
                for (int rep = -3; rep < 5; rep++) { // three untimed JIT warmups
                    final long[] operations = {0};
                    Run result;
                    switch(name) {
                        case "Kruskal" -> result=measure(() -> {var r=SpanningTrees.kruskal(undirected); operations[0]=r.operations(); return r.weight();}, () -> operations[0]);
                        case "Prim" -> result=measure(() -> {var r=SpanningTrees.prim(undirected); operations[0]=r.operations(); return r.weight();}, () -> operations[0]);
                        case "Tarjan_SCC" -> result=measure(() -> {var r=Connectivity.tarjan(directed); operations[0]=r.operations(); return r.groups().size();}, () -> operations[0]);
                        case "Kahn_DAG" -> result=measure(() -> {var r=Connectivity.kahn(dag); operations[0]=r.operations(); return r.order().size();}, () -> operations[0]);
                        case "Kahn_Cyclic" -> result=measure(() -> {var r=Connectivity.kahn(directed); operations[0]=r.operations(); return r.order().size();}, () -> operations[0]);
                        default -> result=measure(() -> {var r=Routes.dijkstra(undirected,0); operations[0]=r.operations(); return r.distance()[v-1];}, () -> operations[0]);
                    }
                    if(rep>=0) { times[rep]=result.ns(); ops[rep]=result.operations(); }
                }
                lines.add(row(name,v,e,times,ops));
            }
        }
        Files.write(table,lines);
        System.out.println("Benchmarks saved: " + table.toAbsolutePath());
        lines.forEach(System.out::println);
    }
}
