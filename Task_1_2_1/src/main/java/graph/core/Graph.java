package graph.core;

import java.util.Set;

public interface Graph<V> {
    boolean isDirected();

    boolean addVertex(V v);

    boolean removeVertex(V v);

    boolean addEdge(V from, V to);

    boolean removeEdge(V from, V to);

    Set<V> vertices();

    Set<V> neighbors(V v);

    boolean equals(Object obj);

    int hashCode();

    String toString();
}
