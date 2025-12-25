package graph.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.core.Graph;
import graph.simple.AdjacencyListGraph;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TopologicalSortTest {

    @Test
    void testKahnSimple() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(4, result.size());
        assertTrue(result.indexOf("A") < result.indexOf("B"));
        assertTrue(result.indexOf("A") < result.indexOf("C"));
        assertTrue(result.indexOf("B") < result.indexOf("D"));
        assertTrue(result.indexOf("C") < result.indexOf("D"));
    }

    @Test
    void testKahnLinear() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(List.of("A", "B", "C", "D"), result);
    }

    @Test
    void testKahnSingleVertex() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addVertex("A");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(List.of("A"), result);
    }

    @Test
    void testKahnComplex() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("C", "E");
        graph.addEdge("D", "F");
        graph.addEdge("E", "F");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(6, result.size());
        assertTrue(result.indexOf("A") < result.indexOf("B"));
        assertTrue(result.indexOf("A") < result.indexOf("C"));
        assertTrue(result.indexOf("B") < result.indexOf("D"));
        assertTrue(result.indexOf("C") < result.indexOf("D"));
        assertTrue(result.indexOf("C") < result.indexOf("E"));
        assertTrue(result.indexOf("D") < result.indexOf("F"));
        assertTrue(result.indexOf("E") < result.indexOf("F"));
    }

    @Test
    void testKahnWithCycleThrowsException() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        assertThrows(IllegalStateException.class, () -> {
            TopologicalSort.kahn(graph);
        });
    }

    @Test
    void testKahnUndirectedGraphThrowsException() {
        Graph<String> graph = new AdjacencyListGraph<>(false);
        graph.addEdge("A", "B");

        assertThrows(IllegalStateException.class, () -> {
            TopologicalSort.kahn(graph);
        });
    }

    @Test
    void testKahnEmptyGraph() {
        Graph<String> graph = new AdjacencyListGraph<>(true);

        List<String> result = TopologicalSort.kahn(graph);

        assertTrue(result.isEmpty());
    }

    @Test
    void testKahnIsolatedVertices() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(3, result.size());
        assertEquals(Set.of("A", "B", "C"), Set.copyOf(result));
    }

    @Test
    void testKahnSelfLoopThrowsException() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "A");

        assertThrows(IllegalStateException.class, () -> {
            TopologicalSort.kahn(graph);
        });
    }

    @Test
    void testKahnPartialCycleThrowsException() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "B");

        assertThrows(IllegalStateException.class, () -> {
            TopologicalSort.kahn(graph);
        });
    }

    @Test
    void testKahnAllVerticesInResult() {
        Graph<String> graph = new AdjacencyListGraph<>(true);
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addVertex("D");

        List<String> result = TopologicalSort.kahn(graph);

        assertEquals(4, result.size());
        assertEquals(Set.of("A", "B", "C", "D"), Set.copyOf(result));
        assertTrue(result.indexOf("A") < result.indexOf("B"));
        assertTrue(result.indexOf("B") < result.indexOf("C"));
    }
}