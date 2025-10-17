package graph.algorithm;

import graph.core.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TopologicalSort {

    private TopologicalSort() {}

    public static <V> List<V> kanh(Graph<V> g) {
        if (!g.isDirected()) {
            throw new IllegalStateException("Topological sorting is defined "
                    + "only for directed graphs.");
        }

        Map<V, Integer> indeg = new LinkedHashMap<>();
        for (V v : g.vertices()) {
            indeg.put(v, 0);
        }
        for (V u : g.vertices()) {
            for (V w : g.neighbors(u)) {
                indeg.put(w, indeg.getOrDefault(w, 0) + 1);
            }
        }

        Deque<V> queue = new ArrayDeque<>();
        for (var e : indeg.entrySet()) {
            if (e.getValue() == 0) {
                queue.add(e.getKey());
            }
        }

        List<V> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            V u = queue.removeFirst();
            order.add(u);

            for (V w : g.neighbors(u)) {
                int newDegree = indeg.get(w) - 1;
                indeg.put(w, newDegree);
                if (newDegree == 0) {
                    queue.add(w);
                }
            }
        }

        if (order.size() != indeg.size()) {
            throw new IllegalStateException("The graph contains a cycle — "
                    + "topological sorting is not possible.");
        }

        return order;
    }
}
