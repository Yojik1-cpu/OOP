package graph.simple;

import graph.core.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class AdjacencyListGraph<V> implements Graph<V> {
    private final boolean directed;
    private final Map<V, Set<V>> adj = new LinkedHashMap<>();

    public AdjacencyListGraph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public boolean addVertex(V v) {
        Objects.requireNonNull(v);
        return adj.putIfAbsent(v, new LinkedHashSet<>()) == null;
    }

    public boolean removeVertex(V v) {
        if (!adj.containsKey(v)) {
            return false;
        }
        adj.remove(v);
        for (Set<V> s : adj.values()) {
            s.remove(v);
        }
        return true;
    }


    public boolean addEdge(V from, V to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        addVertex(from);
        addVertex(to);
        boolean ch = adj.get(from).add(to);
        if (!directed) {
            ch |= adj.get(to).add(from);
        }
        return ch;
    }

    public boolean removeEdge(V from, V to) {
        if (!adj.containsKey(from) || !adj.containsKey(to)) {
            return false;
        }
        boolean ch = adj.get(from).remove(to);
        if (!directed) {
            ch |= adj.get(to).remove(from);
        }
        return ch;
    }

    public Set<V> vertices() {
        return Collections.unmodifiableSet(adj.keySet());
    }

    public Set<V> neighbors(V v) {
        return adj.getOrDefault(v, Collections.emptySet());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AdjacencyListGraph<?> that = (AdjacencyListGraph<?>) obj;
        return directed == that.directed && adj.equals(that.adj);
    }

    public int hashCode() {
        return Objects.hash(directed, adj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AdjacencyListGraph {")
            .append("directed=").append(directed)
            .append(", vertices=").append(vertices())
            .append(", edges=[");

        // Формируем список рёбер в читаемом виде
        List<String> edges = new ArrayList<>();
        for (V from : vertices()) {
            for (V to : neighbors(from)) {
                if (directed || from.toString().compareTo(to.toString()) <= 0) {
                    if (directed) {
                        edges.add(from + " -> " + to);
                    } else {
                        edges.add(from + " -- " + to);
                    }
                }
            }
        }
        sb.append(String.join(", ", edges));
        sb.append("]}");

        return sb.toString();
    }
}