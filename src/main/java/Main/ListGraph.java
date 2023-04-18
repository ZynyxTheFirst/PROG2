package Main;
import java.util.*;
import java.io.Serializable;

public class ListGraph<T> implements Graph<T>, Serializable {

    private final Map<T, Set<Edge>> nodes = new HashMap<>();

    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }
    public boolean pathExists(T from, T to) {
        return false;
    }

    public Collection<Edge<T>> getEdgesFrom(T node) {
        return null;
    }

    public Edge<T> getEdgeBetween(T node1, T node2) {
        return null;
    }

    public List<Edge<T>> getPath(T from, T to) {
        return null;
    }

    public Set<T> getNodes() {
        return null;
    }

    public void connect(T node1, T node2, String name, int weight) {

    }

    public void disconnect(T node1, T node2) {

    }

    public void remove(T node) {

    }

    public void setConnectionWeight(T node1, T node2, int weight) {

    }

    public String toString() {
        return super.toString();
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
