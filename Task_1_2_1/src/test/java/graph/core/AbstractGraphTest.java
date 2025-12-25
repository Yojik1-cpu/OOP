package graph.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.simple.AdjacencyListGraph;
import graph.simple.AdjacencyMatrixGraph;
import graph.simple.IncidenceMatrixGraph;
import org.junit.jupiter.api.Test;

class AbstractGraphTest {

    private static Graph<String> list(boolean directed) {
        return new AdjacencyListGraph<>(directed);
    }

    private static Graph<String> adjMatrix(boolean directed) {
        return new AdjacencyMatrixGraph<>(directed);
    }

    private static Graph<String> incMatrix(boolean directed) {
        return new IncidenceMatrixGraph<>(directed);
    }

    private static void addUndirectedTriangle(Graph<String> g) {
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "A");
    }

    private static void addDirectedChain(Graph<String> g) {
        g.addEdge("A", "B");
        g.addEdge("B", "C");
    }

    @Test
    void equals_isReflexive() {
        Graph<String> g = list(false);
        addUndirectedTriangle(g);

        assertEquals(g, g);
        assertEquals(g.hashCode(), g.hashCode());
    }

    @Test
    void equals_returnsFalseOnNullOrNonGraph() {
        Graph<String> g = list(false);
        addUndirectedTriangle(g);

        assertNotEquals(null, g);
        assertNotEquals("not a graph", g);
    }

    @Test
    void crossTypeEquality_undirected_sameStructure() {
        Graph<String> g1 = list(false);
        Graph<String> g2 = adjMatrix(false);
        Graph<String> g3 = incMatrix(false);

        addUndirectedTriangle(g1);
        addUndirectedTriangle(g2);
        addUndirectedTriangle(g3);

        assertEquals(g1, g2);
        assertEquals(g2, g1);
        assertEquals(g1, g3);
        assertEquals(g3, g1);
        assertEquals(g2, g3);
        assertEquals(g3, g2);

        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g1.hashCode(), g3.hashCode());
    }

    @Test
    void crossTypeEquality_directed_sameStructure() {
        Graph<String> g1 = list(true);
        Graph<String> g2 = adjMatrix(true);
        Graph<String> g3 = incMatrix(true);

        addDirectedChain(g1);
        addDirectedChain(g2);
        addDirectedChain(g3);

        assertEquals(g1, g2);
        assertEquals(g2, g3);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertEquals(g2.hashCode(), g3.hashCode());
    }

    @Test
    void notEqual_whenDirectedFlagDiffers_evenIfEdgesLookSimilar() {
        Graph<String> und = list(false);
        Graph<String> dir = adjMatrix(true);

        und.addEdge("A", "B");
        dir.addEdge("A", "B");
        dir.addEdge("B", "A");

        assertNotEquals(und, dir);
    }

    @Test
    void notEqual_whenVertexSetsDiffer() {
        Graph<String> g1 = list(false);
        Graph<String> g2 = adjMatrix(false);
        g1.addVertex("A");
        g1.addVertex("B");
        g2.addVertex("A");
        g2.addVertex("B");
        g2.addVertex("C");

        assertNotEquals(g1, g2);
    }

    @Test
    void notEqual_whenEdgeSetsDiffer() {
        Graph<String> g1 = list(false);
        Graph<String> g2 = incMatrix(false);

        g1.addVertex("A");
        g1.addVertex("B");
        g1.addVertex("C");
        g2.addVertex("A");
        g2.addVertex("B");
        g2.addVertex("C");

        g1.addEdge("A", "B");
        g2.addEdge("A", "C");

        assertNotEquals(g1, g2);
    }

    @Test
    void symmetric_property_holds() {
        Graph<String> g1 = list(false);
        Graph<String> g2 = adjMatrix(false);

        addUndirectedTriangle(g1);
        addUndirectedTriangle(g2);

        boolean a = g1.equals(g2);
        boolean b = g2.equals(g1);

        assertEquals(a, b);
        assertTrue(a);
    }

    @Test
    void hashChangesWhenGraphChanges() {
        Graph<String> g = list(false);
        g.addEdge("A", "B");

        int h1 = g.hashCode();

        g.addEdge("B", "C");
        int h2 = g.hashCode();

        assertNotEquals(h1, h2);
    }
}
