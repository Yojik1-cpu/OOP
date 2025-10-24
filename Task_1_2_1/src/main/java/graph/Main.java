package graph;

import graph.algorithm.TopologicalSort;
import graph.simple.AdjacencyListGraph;

public class Main {
    public static void main(String[] args) {
        testAdjacencyList();
        testTopologicalSort();
    }

    static void testAdjacencyList() {
        System.out.println("--- AdjacencyListGraph ---");
        var graph = new AdjacencyListGraph<String>(false);

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");

        System.out.println("Vertices: " + graph.vertices());
        System.out.println("A's neighbours: " + graph.neighbors("A"));
        System.out.println("Graph: " + graph);
    }

    static void testTopologicalSort() {
        System.out.println("\n--- Topological Sort ---");
        var graph = new AdjacencyListGraph<String>(true);

        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("A", "G");
        graph.addEdge("G", "C");
        graph.addEdge("C", "B");
        graph.addEdge("B", "C");


        try {
            var result = TopologicalSort.kahn(graph);
            System.out.println("Topological order: " + result);
        } catch (Exception e) {
            System.out.println("Wrong: " + e.getMessage());
        }
    }
}