package Main;

public class Place extends Edge{
    String name;

    public Place(Place destination, String destinationName, int weight, String name){
        super(destination, destinationName, weight);
        this.name = name;
    }
}
