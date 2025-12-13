package graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.simple.AdjacencyListGraph;
import graph.simple.AdjacencyMatrixGraph;
import graph.simple.IncidenceMatrixGraph;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphTest {

    private Graph<String> directedListGraph;
    private Graph<String> undirectedListGraph;
    private Graph<String> directedMatrixGraph;
    private Graph<String> undirectedMatrixGraph;
    private Graph<String> directedIncidenceGraph;
    private Graph<String> undirectedIncidenceGraph;

    @BeforeEach
    void setUp() {
        directedListGraph = new AdjacencyListGraph<>(true);
        undirectedListGraph = new AdjacencyListGraph<>(false);
        directedMatrixGraph = new AdjacencyMatrixGraph<>(true);
        undirectedMatrixGraph = new AdjacencyMatrixGraph<>(false);
        directedIncidenceGraph = new IncidenceMatrixGraph<>(true);
        undirectedIncidenceGraph = new IncidenceMatrixGraph<>(false);
    }

    @Test
    void testAllImplementationsSupportIsDirected() {
        assertTrue(directedListGraph.isDirected());
        assertFalse(undirectedListGraph.isDirected());
        assertTrue(directedMatrixGraph.isDirected());
        assertFalse(undirectedMatrixGraph.isDirected());
        assertTrue(directedIncidenceGraph.isDirected());
        assertFalse(undirectedIncidenceGraph.isDirected());
    }

    @Test
    void testAllImplementationsSupportAddVertex() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertTrue(graph.addVertex("A"));
            assertTrue(graph.addVertex("B"));
            assertFalse(graph.addVertex("A"));
            assertEquals(Set.of("A", "B"), graph.vertices());
        }
    }

    @Test
    void testAllImplementationsRejectNullVertex() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertThrows(NullPointerException.class, () -> {
                graph.addVertex(null);
            });
        }
    }

    @Test
    void testAllImplementationsSupportRemoveVertex() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            graph.addVertex("A");
            graph.addVertex("B");
            graph.addVertex("C");
            graph.addEdge("A", "B");
            graph.addEdge("B", "C");

            assertTrue(graph.removeVertex("A"));
            assertFalse(graph.removeVertex("D"));
            assertEquals(Set.of("B", "C"), graph.vertices());
            assertEquals(Set.of("C"), graph.neighbors("B"));
        }
    }

    @Test
    void testAllImplementationsSupportAddEdge() {
        // directed
        Graph<String>[] directedGraphs = new Graph[]{
            directedListGraph, directedMatrixGraph, directedIncidenceGraph
        };

        for (Graph<String> graph : directedGraphs) {
            assertTrue(graph.addEdge("A", "B"));
            assertFalse(graph.addEdge("A", "B")); // duplicate
            assertEquals(Set.of("B"), graph.neighbors("A"));
            assertEquals(Set.of(), graph.neighbors("B"));
        }

        // undirected
        Graph<String>[] undirectedGraphs = new Graph[]{
            undirectedListGraph, undirectedMatrixGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : undirectedGraphs) {
            assertTrue(graph.addEdge("A", "B"));
            assertFalse(graph.addEdge("A", "B")); // duplicate
            assertFalse(graph.addEdge("B", "A")); // also duplicate for undirected
            assertEquals(Set.of("B"), graph.neighbors("A"));
            assertEquals(Set.of("A"), graph.neighbors("B"));
        }
    }

    @Test
    void testAllImplementationsRejectNullEdge() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertThrows(NullPointerException.class, () ->
                    graph.addEdge(null, "B"));
            assertThrows(NullPointerException.class, () ->
                    graph.addEdge("A", null));
        }
    }

    @Test
    void testAllImplementationsSupportRemoveEdge() {
        // directed
        Graph<String>[] directedGraphs = new Graph[]{
            directedListGraph, directedMatrixGraph, directedIncidenceGraph
        };

        for (Graph<String> graph : directedGraphs) {
            graph.addEdge("A", "B");
            graph.addEdge("B", "C");

            assertTrue(graph.removeEdge("A", "B"));
            assertFalse(graph.removeEdge("A", "B"));
            assertFalse(graph.removeEdge("X", "Y"));
            assertEquals(Set.of(), graph.neighbors("A"));
            assertEquals(Set.of("C"), graph.neighbors("B"));
        }

        // undirected
        Graph<String>[] undirectedGraphs = new Graph[]{
            undirectedListGraph, undirectedMatrixGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : undirectedGraphs) {
            graph.addEdge("A", "B");
            graph.addEdge("B", "C");

            assertTrue(graph.removeEdge("A", "B"));
            assertFalse(graph.removeEdge("A", "B"));
            assertFalse(graph.removeEdge("B", "A"));
            assertEquals(Set.of(), graph.neighbors("A"));
            assertEquals(Set.of("C"), graph.neighbors("B"));
        }
    }

    @Test
    void testAllImplementationsReturnEmptyVerticesForNewGraph() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertEquals(Collections.emptySet(), graph.vertices());
        }
    }

    @Test
    void testAllImplementationsSupportNeighbors() {
        // directed
        Graph<String>[] directedGraphs = new Graph[]{
            directedListGraph, directedMatrixGraph, directedIncidenceGraph
        };

        for (Graph<String> graph : directedGraphs) {
            graph.addEdge("A", "B");
            graph.addEdge("A", "C");
            graph.addEdge("B", "C");

            assertEquals(Set.of("B", "C"), graph.neighbors("A"));
            assertEquals(Set.of("C"), graph.neighbors("B"));
            assertEquals(Set.of(), graph.neighbors("C"));
            assertEquals(Collections.emptySet(), graph.neighbors("X"));
        }

        // undirected
        Graph<String>[] undirectedGraphs = new Graph[]{
            undirectedListGraph, undirectedMatrixGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : undirectedGraphs) {
            graph.addEdge("A", "B");
            graph.addEdge("A", "C");
            graph.addEdge("B", "C");

            assertEquals(Set.of("B", "C"), graph.neighbors("A"));
            assertEquals(Set.of("A", "C"), graph.neighbors("B"));
            assertEquals(Set.of("A", "B"), graph.neighbors("C"));
        }
    }

    @Test
    void sameDirGraphsAcrossImplementationsAreEqual() {
        Graph<String> g1 = new AdjacencyListGraph<>(true);
        Graph<String> g2 = new AdjacencyMatrixGraph<>(true);
        Graph<String> g3 = new IncidenceMatrixGraph<>(true);

        g1.addEdge("A", "B");
        g2.addEdge("A", "B");
        g3.addEdge("A", "B");

        assertEquals(g1, g2);
        assertEquals(g2, g3);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g2.hashCode(), g3.hashCode());
    }


    @Test
    void testAllImplementationsHandleComplexGraphStructure() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, directedMatrixGraph, directedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            graph.addEdge("A", "B");
            graph.addEdge("A", "C");
            graph.addEdge("B", "D");
            graph.addEdge("C", "D");
            graph.addEdge("D", "E");

            assertEquals(Set.of("B", "C"), graph.neighbors("A"));
            assertEquals(Set.of("D"), graph.neighbors("B"));
            assertEquals(Set.of("D"), graph.neighbors("C"));
            assertEquals(Set.of("E"), graph.neighbors("D"));
            assertEquals(Set.of(), graph.neighbors("E"));

            // remove a vertex in the middle
            assertTrue(graph.removeVertex("D"));
            assertEquals(Set.of("B", "C"), graph.neighbors("A"));
            assertEquals(Set.of(), graph.neighbors("B"));
            assertEquals(Set.of(), graph.neighbors("C"));
            assertEquals(Set.of("A", "B", "C", "E"), graph.vertices());
        }
    }

    @Test
    void testAllImplementationsSupportSelfLoops() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertTrue(graph.addEdge("A", "A"));
            assertEquals(Set.of("A"), graph.neighbors("A"));
        }
    }

    @Test
    void testAllImplementationsPreventDuplicateEdges() {
        Graph<String>[] graphs = new Graph[]{
            directedListGraph, undirectedListGraph,
            directedMatrixGraph, undirectedMatrixGraph,
            directedIncidenceGraph, undirectedIncidenceGraph
        };

        for (Graph<String> graph : graphs) {
            assertTrue(graph.addEdge("A", "B"));
            assertFalse(graph.addEdge("A", "B"));
        }
    }
}