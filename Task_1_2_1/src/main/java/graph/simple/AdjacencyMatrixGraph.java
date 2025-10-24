package graph.simple;

import graph.core.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AdjacencyMatrixGraph<V> implements Graph<V> {
    private final boolean directed;
    private final List<V> index = new ArrayList<>();
    private final Map<V, Integer> pos = new HashMap<>();
    private boolean[][] matrix = new boolean[0][0];

    public AdjacencyMatrixGraph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean addVertex(V v) {
        Objects.requireNonNull(v);
        if (pos.containsKey(v)) {
            return false;
        }
        pos.put(v, index.size());
        index.add(v);
        int n = index.size();
        boolean[][] nm = new boolean[n][n];
        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(matrix[i], 0, nm[i], 0, n - 1);
        }
        matrix = nm;
        return true;
    }

    public boolean removeVertex(V v) {
        Integer p = pos.remove(v);
        if (p == null) {
            return false;
        }
        index.remove((int) p);
        for (int i = p; i < index.size(); i++) {
            pos.put(index.get(i), i);
        }
        int n = index.size();
        boolean[][] nm = new boolean[n][n];
        for (int i = 0, ii = 0; i <= n; i++) {
            if (i == p) {
                continue;
            }
            for (int j = 0, jj = 0; j <= n; j++) {
                if (j == p) {
                    continue;
                }
                nm[ii][jj] = matrix[i][j];
                jj++;
            }
            ii++;
        }
        matrix = nm;
        return true;
    }

    public boolean addEdge(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        addVertex(from);
        addVertex(to);
        int i = pos.get(from);
        int j = pos.get(to);
        boolean ch = !matrix[i][j];
        matrix[i][j] = true;
        if (!directed) {
            matrix[j][i] = true;
        }
        return ch;
    }

    public boolean removeEdge(V from, V to) {
        Integer i = pos.get(from);
        Integer j = pos.get(to);
        if (i == null || j == null) {
            return false;
        }
        boolean ch = matrix[i][j];
        matrix[i][j] = false;
        if (!directed) {
            ch |= matrix[j][i];
            matrix[j][i] = false;
        }
        return ch;
    }

    public Set<V> vertices() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(index));
    }

    public Set<V> neighbors(V v) {
        Integer i = pos.get(v);
        if (i == null) {
            return Collections.emptySet();
        }
        Set<V> res = new LinkedHashSet<>();
        for (int j = 0; j < index.size(); j++) {
            if (matrix[i][j]) {
                res.add(index.get(j));
            }
        }
        return res;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AdjacencyMatrixGraph<?> that = (AdjacencyMatrixGraph<?>) obj;
        return directed == that.directed
                && index.equals(that.index)
                && Arrays.deepEquals(matrix, that.matrix);
    }

    public int hashCode() {
        int result = Objects.hash(directed, index);
        result = 31 * result + Arrays.deepHashCode(matrix);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AdjacencyMatrixGraph {")
                .append("directed=").append(directed)
                .append(", vertices=").append(index)
                .append(", matrix=\n");

        sb.append("    ");
        for (V vertex : index) {
            sb.append(String.format("%-4s", vertex));
        }
        sb.append("\n");

        for (int i = 0; i < index.size(); i++) {
            sb.append(String.format("%-4s", index.get(i)));
            for (int j = 0; j < index.size(); j++) {
                if (matrix[i][j]) {
                    sb.append("1   ");
                } else {
                    sb.append("0   ");
                }
            }
            sb.append("\n");
        }
        sb.append("}");

        return sb.toString();
    }
}