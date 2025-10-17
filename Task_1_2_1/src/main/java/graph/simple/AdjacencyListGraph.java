package graph.simple;

import graph.core.Graph;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
}