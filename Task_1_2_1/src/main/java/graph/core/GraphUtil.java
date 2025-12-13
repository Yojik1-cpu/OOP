package graph.core;

import java.util.Objects;
import java.util.Set;

public final class GraphUtil {
    private GraphUtil() {
    }

    public static boolean graphEquals(Graph<?> g, Object obj) {
        if (g == obj) {
            return true;
        }

        if (!(obj instanceof Graph<?> other)) {
            return false;
        }

        if (g.isDirected() != other.isDirected()) {
            return false;
        }

        if (!g.vertices().equals(other.vertices())) {
            return false;
        }

        for (Object v : g.vertices()) {
            Set<?> n1 = (Set<?>) ((Graph) g).neighbors(v);
            Set<?> n2 = (Set<?>) ((Graph) other).neighbors(v);

            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    public static int graphHash(Graph<?> g) {
        int h = Boolean.hashCode(g.isDirected());
        h = 31 * h + g.vertices().hashCode();

        int acc = 0;
        for (Object v : g.vertices()) {
            Set<?> n = (Set<?>) ((Graph) g).neighbors(v);
            acc += Objects.hash(v, n);
        }
        return 31 * h + acc;
    }
}
