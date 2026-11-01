package city;

import java.util.*;

public final class Main {
    private static final String[] DISTRICTS = {
        "Solar Square", "River Gardens", "Eco Campus", "Innovation Park", "Central Station", "Wind District"
    };
    public static void main(String[] args) throws Exception {
        System.out.println("MEREYLIM'S GREEN MOBILITY NETWORK\n");
        Checks.run();
        Graph city = new Graph(DISTRICTS.length,false);
        city.add(0,1,4); city.add(0,2,7); city.add(1,2,2); city.add(1,3,6);
        city.add(2,4,3); city.add(3,4,1); city.add(3,5,5); city.add(4,5,4);
        System.out.println("Districts: " + Arrays.toString(DISTRICTS));
        System.out.println("Kruskal MST: " + SpanningTrees.kruskal(city));
        System.out.println("Prim MST:    " + SpanningTrees.prim(city));
        Routes.Result routes = Routes.dijkstra(city, 0);
        for(int i=0;i<DISTRICTS.length;i++) {
            System.out.print(DISTRICTS[0] + " -> " + DISTRICTS[i] + ": distance="+routes.distance()[i]+" path=");
            System.out.println(routes.path(i).stream().map(j -> DISTRICTS[j]).toList());
        }
        Graph serviceDependencies = new Graph(6,true);
        serviceDependencies.add(0,1,1);serviceDependencies.add(0,2,1);serviceDependencies.add(1,3,1);
        serviceDependencies.add(2,4,1);serviceDependencies.add(3,5,1);serviceDependencies.add(4,5,1);
        System.out.println("Infrastructure rollout order: " + Connectivity.kahn(serviceDependencies));
        Graph oneWay = new Graph(6,true);
        oneWay.add(0,1,1);oneWay.add(1,2,1);oneWay.add(2,0,1);
        oneWay.add(2,3,1);oneWay.add(3,4,1);oneWay.add(4,3,1);oneWay.add(4,5,1);
        System.out.println("One-way road SCCs: " + Connectivity.tarjan(oneWay));
        Experiments.run();
    }
}
