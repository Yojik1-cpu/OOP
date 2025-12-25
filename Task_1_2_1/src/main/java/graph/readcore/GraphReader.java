package graph.readcore;

import graph.core.Graph;
import graph.simple.AdjacencyListGraph;
import graph.simple.AdjacencyMatrixGraph;
import graph.simple.IncidenceMatrixGraph;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphReader {

    public enum GraphType {
        ADJACENCY_MATRIX, ADJACENCY_LIST, INCIDENCE_MATRIX
    }

    public static Graph<String> readFromFile(String filename, GraphType type) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            //directed/undirected
            String directedLine = reader.readLine();
            boolean directed = "directed".equalsIgnoreCase(directedLine.trim());

            Graph<String> graph = createGraph(type, directed);

            //количество вершин
            int vertexCount = Integer.parseInt(reader.readLine().trim());

            //вершины
            for (int i = 0; i < vertexCount; i++) {
                String vertex = reader.readLine().trim();
                graph.addVertex(vertex);
            }

            //количество рёбер
            int edgeCount = Integer.parseInt(reader.readLine().trim());

            //рёбра
            for (int i = 0; i < edgeCount; i++) {
                String[] edge = reader.readLine().trim().split("\\s+");
                if (edge.length >= 2) {
                    graph.addEdge(edge[0], edge[1]);
                }
            }

            return graph;
        }
    }

    private static Graph<String> createGraph(GraphType type, boolean directed) {
        return switch (type) {
            case ADJACENCY_MATRIX -> new AdjacencyMatrixGraph<>(directed);
            case ADJACENCY_LIST -> new AdjacencyListGraph<>(directed);
            case INCIDENCE_MATRIX -> new IncidenceMatrixGraph<>(directed);
        };
    }
}