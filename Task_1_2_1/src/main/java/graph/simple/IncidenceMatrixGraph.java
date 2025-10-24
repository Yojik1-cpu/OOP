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

public class IncidenceMatrixGraph<V> implements Graph<V> {
    private final boolean directed;
    private final List<V> index = new ArrayList<>();
    private final Map<V, Integer> pos = new HashMap<>();
    private final List<int[]> edges = new ArrayList<>();

    public IncidenceMatrixGraph(boolean directed) {
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
        rebuildMatrix();
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
        edges.removeIf(e -> e[0] == p || e[1] == p);
        for (int[] e : edges) {
            if (e[0] > p) {
                e[0]--;
            }
            if (e[1] > p) {
                e[1]--;
            }
        }
        rebuildMatrix();
        return true;
    }

    public boolean addEdge(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        addVertex(from);
        addVertex(to);
        int u = pos.get(from);
        int v = pos.get(to);
        for (int[] e : edges) {
            if ((e[0] == u && e[1] == v) || (!directed && e[0] == v && e[1] == u)) {
                return false;
            }
        }
        edges.add(new int[]{u, v});
        rebuildMatrix();
        return true;
    }

    public boolean removeEdge(V from, V to) {
        Integer u = pos.get(from);
        Integer v = pos.get(to);
        if (u == null || v == null) {
            return false;
        }
        boolean removed = edges.removeIf(e -> (e[0] == u && e[1] == v)
                || (!directed && e[0] == v && e[1] == u));
        if (removed) {
            rebuildMatrix();
        }
        return removed;
    }

    public Set<V> vertices() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(index));
    }

    public Set<V> neighbors(V v) {
        Integer p = pos.get(v);
        if (p == null) {
            return Collections.emptySet();
        }
        Set<V> res = new LinkedHashSet<>();
        for (int[] e : edges) {
            if (directed) {
                if (e[0] == p) {
                    res.add(index.get(e[1]));
                }
            } else {
                if (e[0] == p) {
                    res.add(index.get(e[1]));
                } else if (e[1] == p) {
                    res.add(index.get(e[0]));
                }
            }
        }
        return res;
    }

    private void rebuildMatrix() {
        int vrt = index.size();
        int edg = edges.size();
        int[][] matrix = new int[vrt][edg];
        for (int e = 0; e < edg; e++) {
            int u = edges.get(e)[0];
            int v = edges.get(e)[1];
            if (directed) {
                matrix[u][e] = +1;
                matrix[v][e] = -1;
            } else {
                matrix[u][e] = 1;
                matrix[v][e] = 1;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IncidenceMatrixGraph<?> that = (IncidenceMatrixGraph<?>) obj;
        if (directed != that.directed || !index.equals(that.index)) {
            return false;
        }
        if (edges.size() != that.edges.size()) {
            return false;
        }
        for (int i = 0; i < edges.size(); i++) {
            if (!Arrays.equals(edges.get(i), that.edges.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(directed, index);
        for (int[] edge : edges) {
            result = 31 * result + Arrays.hashCode(edge);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IncidenceMatrixGraph {")
            .append("directed=").append(directed)
            .append(", vertices=").append(index)
            .append(", edges=[");

        List<String> edgeStrings = new ArrayList<>();
        for (int[] edge : edges) {
            String from = index.get(edge[0]).toString();
            String to = index.get(edge[1]).toString();
            if (directed) {
                edgeStrings.add(from + " -> " + to);
            } else {
                edgeStrings.add(from + " -- " + to);
            }
        }
        sb.append(String.join(", ", edgeStrings));
        sb.append("]}");

        return sb.toString();
    }
}