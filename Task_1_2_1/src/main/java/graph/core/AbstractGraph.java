package graph.core;

import java.util.Objects;
import java.util.Set;

public abstract class AbstractGraph<V> implements Graph<V> {

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Graph<?> other)) {
            return false;
        }

        if (this.isDirected() != other.isDirected()) {
            return false;
        }
        if (!this.vertices().equals(other.vertices())) {
            return false;
        }

        for (V v : this.vertices()) {
            Set<V> n1 = this.neighbors(v);
            Set<?> n2 = (Set<?>) ((Graph) other).neighbors(v);

            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public final int hashCode() {
        int h = Boolean.hashCode(this.isDirected());
        h = 31 * h + this.vertices().hashCode();

        int acc = 0;
        for (V v : this.vertices()) {
            acc += Objects.hash(v, this.neighbors(v));
        }
        return 31 * h + acc;
    }
}

