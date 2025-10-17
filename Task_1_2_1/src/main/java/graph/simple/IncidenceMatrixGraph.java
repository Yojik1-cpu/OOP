package graph.simple;

import graph.core.Graph;

import java.util.*;

public class IncidenceMatrixGraph<V> implements Graph<V> {
    private final boolean directed;
    private final List<V> index = new ArrayList<>();
    private final Map<V, Integer> pos = new HashMap<>();

    private final List<int[]> edges = new ArrayList<>();
    private int[][] matrix = new int[0][0];

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
        int nV = index.size();
        int nE = edges.size();
        matrix = new int[nV][nE];
        for (int e = 0; e < nE; e++) {
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
}