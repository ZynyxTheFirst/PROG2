public class Edge<T> {
    private final T destination;
    private final String name;
    private final int weight;

    public Edge(T destination, String name, int weight){
        this.name = name;
        this.destination = destination;
        this.weight = weight;
    }

    public T getDestination() {
        return destination;
    }
    public int getWeight(){
        return weight;
    }
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return destination + ", " + name + ", " + weight;
    }
}