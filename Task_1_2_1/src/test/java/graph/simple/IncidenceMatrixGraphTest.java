package graph.simple;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;
import java.util.Collections;

class IncidenceMatrixGraphTest {

    private IncidenceMatrixGraph<String> directedGraph;
    private IncidenceMatrixGraph<String> undirectedGraph;

    @BeforeEach
    void setUp() {
        directedGraph = new IncidenceMatrixGraph<>(true);
        undirectedGraph = new IncidenceMatrixGraph<>(false);
    }

    @Test
    void testIsDirected() {
        assertTrue(directedGraph.isDirected());
        assertFalse(undirectedGraph.isDirected());
    }

    @Test
    void testAddVertex() {
        assertTrue(directedGraph.addVertex("A"));
        assertTrue(directedGraph.addVertex("B"));
        assertFalse(directedGraph.addVertex("A"));
        assertEquals(Set.of("A", "B"), directedGraph.vertices());
    }

    @Test
    void testAddVertexNull() {
        assertThrows(NullPointerException.class, () -> {
            directedGraph.addVertex(null);
        });
    }

    @Test
    void testRemoveVertex() {
        directedGraph.addVertex("A");
        directedGraph.addVertex("B");
        directedGraph.addVertex("C");
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("B", "C");

        assertTrue(directedGraph.removeVertex("A"));
        assertFalse(directedGraph.removeVertex("D"));
        assertEquals(Set.of("B", "C"), directedGraph.vertices());
        assertEquals(Set.of("C"), directedGraph.neighbors("B"));
    }

    @Test
    void testRemoveVertexWithEdges() {
        directedGraph.addVertex("A");
        directedGraph.addVertex("B");
        directedGraph.addVertex("C");
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("B", "C");
        directedGraph.addEdge("A", "C");

        assertTrue(directedGraph.removeVertex("B"));
        assertEquals(Set.of("A", "C"), directedGraph.vertices());
        assertEquals(Set.of("C"), directedGraph.neighbors("A"));
        assertEquals(Set.of(), directedGraph.neighbors("C"));
    }

    @Test
    void testAddEdgeDirected() {
        assertTrue(directedGraph.addEdge("A", "B"));
        assertFalse(directedGraph.addEdge("A", "B"));
        assertEquals(Set.of("B"), directedGraph.neighbors("A"));
        assertEquals(Set.of(), directedGraph.neighbors("B"));
    }

    @Test
    void testAddEdgeUndirected() {
        assertTrue(undirectedGraph.addEdge("A", "B"));
        assertFalse(undirectedGraph.addEdge("A", "B"));
        assertFalse(undirectedGraph.addEdge("B", "A"));
        assertEquals(Set.of("B"), undirectedGraph.neighbors("A"));
        assertEquals(Set.of("A"), undirectedGraph.neighbors("B"));
    }

    @Test
    void testAddEdgeNull() {
        assertThrows(NullPointerException.class, () -> {
            directedGraph.addEdge(null, "B");
        });
        assertThrows(NullPointerException.class, () -> {
            directedGraph.addEdge("A", null);
        });
    }

    @Test
    void testRemoveEdgeDirected() {
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("B", "C");

        assertTrue(directedGraph.removeEdge("A", "B"));
        assertFalse(directedGraph.removeEdge("A", "B"));
        assertFalse(directedGraph.removeEdge("X", "Y"));
        assertEquals(Set.of(), directedGraph.neighbors("A"));
        assertEquals(Set.of("C"), directedGraph.neighbors("B"));
    }

    @Test
    void testRemoveEdgeUndirected() {
        undirectedGraph.addEdge("A", "B");
        undirectedGraph.addEdge("B", "C");

        assertTrue(undirectedGraph.removeEdge("A", "B"));
        assertFalse(undirectedGraph.removeEdge("A", "B"));
        assertFalse(undirectedGraph.removeEdge("B", "A"));
        assertEquals(Set.of(), undirectedGraph.neighbors("A"));
        assertEquals(Set.of("C"), undirectedGraph.neighbors("B"));
    }

    @Test
    void testVerticesEmpty() {
        assertEquals(Collections.emptySet(), directedGraph.vertices());
    }

    @Test
    void testVerticesAfterOperations() {
        directedGraph.addVertex("A");
        directedGraph.addVertex("B");
        directedGraph.addEdge("A", "C");
        directedGraph.removeVertex("B");

        assertEquals(Set.of("A", "C"), directedGraph.vertices());
    }

    @Test
    void testNeighborsDirected() {
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("A", "C");
        directedGraph.addEdge("B", "C");

        assertEquals(Set.of("B", "C"), directedGraph.neighbors("A"));
        assertEquals(Set.of("C"), directedGraph.neighbors("B"));
        assertEquals(Set.of(), directedGraph.neighbors("C"));
        assertEquals(Collections.emptySet(), directedGraph.neighbors("X"));
    }

    @Test
    void testNeighborsUndirected() {
        undirectedGraph.addEdge("A", "B");
        undirectedGraph.addEdge("A", "C");
        undirectedGraph.addEdge("B", "C");

        assertEquals(Set.of("B", "C"), undirectedGraph.neighbors("A"));
        assertEquals(Set.of("A", "C"), undirectedGraph.neighbors("B"));
        assertEquals(Set.of("A", "B"), undirectedGraph.neighbors("C"));
    }

    @Test
    void testEqualsSameGraph() {
        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("B", "C");

        IncidenceMatrixGraph<String> other = new IncidenceMatrixGraph<>(true);
        other.addEdge("A", "B");
        other.addEdge("B", "C");

        assertEquals(directedGraph, other);
        assertEquals(directedGraph.hashCode(), other.hashCode());
    }

    @Test
    void testEqualsDifferentDirected() {
        directedGraph.addEdge("A", "B");

        IncidenceMatrixGraph<String> undirected = new IncidenceMatrixGraph<>(false);
        undirected.addEdge("A", "B");

        assertNotEquals(directedGraph, undirected);
    }

    @Test
    void testEqualsDifferentEdges() {
        directedGraph.addEdge("A", "B");

        IncidenceMatrixGraph<String> other = new IncidenceMatrixGraph<>(true);
        other.addEdge("A", "C");

        assertNotEquals(directedGraph, other);
    }

    @Test
    void testEqualsDifferentVertices() {
        directedGraph.addEdge("A", "B");

        IncidenceMatrixGraph<String> other = new IncidenceMatrixGraph<>(true);
        other.addEdge("X", "Y");

        assertNotEquals(directedGraph, other);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(null, directedGraph);
    }

    @Test
    void testEqualsDifferentClass() {
        assertNotEquals("not a graph", directedGraph);
    }

    @Test
    void testComplexGraphStructure() {

        directedGraph.addEdge("A", "B");
        directedGraph.addEdge("A", "C");
        directedGraph.addEdge("B", "D");
        directedGraph.addEdge("C", "D");
        directedGraph.addEdge("D", "E");

        assertEquals(Set.of("B", "C"), directedGraph.neighbors("A"));
        assertEquals(Set.of("D"), directedGraph.neighbors("B"));
        assertEquals(Set.of("D"), directedGraph.neighbors("C"));
        assertEquals(Set.of("E"), directedGraph.neighbors("D"));
        assertEquals(Set.of(), directedGraph.neighbors("E"));

        //remove a vertex in the middle
        assertTrue(directedGraph.removeVertex("D"));
        assertEquals(Set.of("B", "C"), directedGraph.neighbors("A"));
        assertEquals(Set.of(), directedGraph.neighbors("B"));
        assertEquals(Set.of(), directedGraph.neighbors("C"));
        assertEquals(Set.of("A", "B", "C", "E"), directedGraph.vertices());
    }

    @Test
    void testSelfLoopDirected() {
        assertTrue(directedGraph.addEdge("A", "A"));
        assertEquals(Set.of("A"), directedGraph.neighbors("A"));
    }

    @Test
    void testSelfLoopUndirected() {
        assertTrue(undirectedGraph.addEdge("A", "A"));
        assertEquals(Set.of("A"), undirectedGraph.neighbors("A"));
    }

    @Test
    void testMultipleEdgesBetweenSameVertices() {
        directedGraph.addEdge("A", "B");
        assertFalse(directedGraph.addEdge("A", "B"));

        undirectedGraph.addEdge("A", "B");
        assertFalse(undirectedGraph.addEdge("A", "B"));
        assertFalse(undirectedGraph.addEdge("B", "A"));
    }

    @Test
    void testToString() {
        directedGraph.addEdge("A", "B");
        String result = directedGraph.toString();
        assertNotNull(result);
        assertTrue(result.contains("IncidenceMatrixGraph"));
    }
}