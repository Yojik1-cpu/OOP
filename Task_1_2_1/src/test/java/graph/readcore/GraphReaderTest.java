package graph.readcore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graph.core.Graph;
import graph.simple.AdjacencyListGraph;
import graph.simple.AdjacencyMatrixGraph;
import graph.simple.IncidenceMatrixGraph;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class GraphReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testReadAdjacencyListDirected() throws IOException {
        String content = """
                directed
                3
                A
                B
                C
                2
                A B
                B C""";
        Path testFile = tempDir.resolve("test_directed.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertTrue(graph instanceof AdjacencyListGraph);
        assertTrue(graph.isDirected());
        assertEquals(Set.of("A", "B", "C"), graph.vertices());
        assertEquals(Set.of("B"), graph.neighbors("A"));
        assertEquals(Set.of("C"), graph.neighbors("B"));
    }

    @Test
    void testReadAdjacencyListUndirected() throws IOException {
        String content = """
                undirected
                3
                A
                B
                C
                2
                A B
                B C""";
        Path testFile = tempDir.resolve("test_undirected.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertTrue(graph instanceof AdjacencyListGraph);
        assertFalse(graph.isDirected());
        assertEquals(Set.of("A", "B", "C"), graph.vertices());
        assertEquals(Set.of("B"), graph.neighbors("A"));
        assertEquals(Set.of("A", "C"), graph.neighbors("B"));
    }

    @Test
    void testReadAdjacencyMatrix() throws IOException {
        String content = """
                directed
                3
                X
                Y
                Z
                2
                X Y
                Y Z""";
        Path testFile = tempDir.resolve("test_matrix.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_MATRIX);

        assertTrue(graph instanceof AdjacencyMatrixGraph);
        assertTrue(graph.isDirected());
        assertEquals(Set.of("X", "Y", "Z"), graph.vertices());
        assertEquals(Set.of("Y"), graph.neighbors("X"));
        assertEquals(Set.of("Z"), graph.neighbors("Y"));
    }

    @Test
    void testReadIncidenceMatrix() throws IOException {
        String content = """
                undirected
                3
                Node1
                Node2
                Node3
                2
                Node1 Node2
                Node2 Node3""";
        Path testFile = tempDir.resolve("test_incidence.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.INCIDENCE_MATRIX);

        assertTrue(graph instanceof IncidenceMatrixGraph);
        assertFalse(graph.isDirected());
        assertEquals(Set.of("Node1", "Node2", "Node3"), graph.vertices());
        assertEquals(Set.of("Node2"), graph.neighbors("Node1"));
        assertEquals(Set.of("Node1", "Node3"), graph.neighbors("Node2"));
    }

    @Test
    void testReadComplexGraph() throws IOException {
        String content = """
                directed
                3
                Start
                Middle
                End
                3
                Start Middle
                Middle End
                Start End""";
        Path testFile = tempDir.resolve("test_complex.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertTrue(graph.isDirected());
        assertEquals(Set.of("Start", "Middle", "End"), graph.vertices());
        assertEquals(Set.of("Middle", "End"), graph.neighbors("Start"));
        assertEquals(Set.of("End"), graph.neighbors("Middle"));
    }

    @Test
    void testReadFileWithExtraSpaces() throws IOException {
        String content = "  directed  \n2\n  VertexA  \n  VertexB  \n1\n  VertexA    VertexB  \n";
        Path testFile = tempDir.resolve("test_spaces.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertTrue(graph.isDirected());
        assertEquals(Set.of("VertexA", "VertexB"), graph.vertices());
        assertEquals(Set.of("VertexB"), graph.neighbors("VertexA"));
    }

    @Test
    void testReadEmptyGraph() throws IOException {
        String content = "directed\n0\n0";
        Path testFile = tempDir.resolve("test_empty.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertNotNull(graph);
        assertTrue(graph.vertices().isEmpty());
    }

    @Test
    void testReadSingleVertex() throws IOException {
        String content = "undirected\n1\nSingle\n0";
        Path testFile = tempDir.resolve("test_single.txt");
        Files.writeString(testFile, content);

        Graph<String> graph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);

        assertEquals(Set.of("Single"), graph.vertices());
        assertEquals(Set.of(), graph.neighbors("Single"));
    }

    @Test
    void testFileNotFound() {
        assertThrows(IOException.class, () -> {
            GraphReader.readFromFile("nonexistent_file.txt", GraphReader.GraphType.ADJACENCY_LIST);
        });
    }

    @Test
    void testInvalidFormatNonNumericVertexCount() throws IOException {
        String content = "directed\nnot_a_number\nA\nB";
        Path testFile = tempDir.resolve("test_invalid.txt");
        Files.writeString(testFile, content);

        assertThrows(NumberFormatException.class, () -> {
            GraphReader.readFromFile(testFile.toString(), GraphReader.GraphType.ADJACENCY_LIST);
        });
    }

    @Test
    void testAllGraphTypesProduceSameResults() throws IOException {
        String content = """
                undirected
                3
                A
                B
                C
                2
                A B
                B C""";
        Path testFile = tempDir.resolve("test_consistency.txt");
        Files.writeString(testFile, content);

        Graph<String> listGraph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_LIST);
        Graph<String> matrixGraph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.ADJACENCY_MATRIX);
        Graph<String> incidenceGraph = GraphReader.readFromFile(testFile.toString(),
                GraphReader.GraphType.INCIDENCE_MATRIX);

        assertEquals(listGraph.vertices(), matrixGraph.vertices());
        assertEquals(listGraph.vertices(), incidenceGraph.vertices());

        for (String vertex : listGraph.vertices()) {
            assertEquals(listGraph.neighbors(vertex), matrixGraph.neighbors(vertex));
            assertEquals(listGraph.neighbors(vertex), incidenceGraph.neighbors(vertex));
        }
    }
}