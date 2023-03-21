package edu.kit.kastel.trafficsimulation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

public class Street {

    /** The length of the street in meters (min 5, max 40)*/
    private int length;
    
    /** The max speed allowed on the street in m/tick (min 5, max 40) */
    private int maxSpeed;

    private int type;

    private int startNode;

    private int endNode;

    /**
     * This Hashmap stores all cars that are currently on a street.
     * The Key in this map is the current distance the cars has to the EndNode.
     * The reason for it being a HashMap is that the task explicitly states
     * that cars are updated in order (with the last car coming first).
     * Additionally we know that there are never two cars at one spot at 
     * the same time because it is a given that cars have to keep a 10m distance 
     * to each other. This way we can use .keySet() (and maybe sort?) to iterate through all cars, starting //TODO do we need to sort? look at the hashmap implementation in java 17
     * with the lowest key (car with the lowest distance to the EndNode).
     */
    private NavigableMap<Integer, Car> cars = new TreeMap<>();


    public Street(int startNode, int endNode, int length, int type, int maxSpeed) { 
        
        if (length < 10 || length > 10000) {
            throw new IllegalArgumentException("Error: the length of a street has to be at least 10 and a most 10000");
        }
        this.length = length;

        if (maxSpeed < 5 || maxSpeed > 40) {
            throw new IllegalArgumentException("Error: the max speed allowed on a street has to be at least 5 and at most 40");
        }

        this.type = type;

        this.startNode = startNode;
        this.endNode = endNode;

    }

    public void updateCars() {

        ArrayList<Integer> keySet = new ArrayList<Integer>(cars.keySet());
        Collections.sort(keySet);

        NavigableMap<Integer, Car> updatedMap = new TreeMap<>();

        
        
        Entry<Integer, Car> entry;
        while ((entry = cars.pollFirstEntry()) != null) {
            Integer position = entry.getKey();
            Car car = entry.getValue();

            Entry<Integer, Car> nextCar = cars.higherEntry(entry.getKey());
            Entry<Integer, Car> carInFrontOfMe;
            //Check if I was overtaken
            int carInFrontOfMePosition;
            if ((carInFrontOfMe = updatedMap.higherEntry(position)) != null) {
                //I was overtaken
                //Get position of car in front of me
                carInFrontOfMePosition = carInFrontOfMe.getKey();
                
            } else {
                //I was not overtaken
                //Get position of next car in iteration list
                carInFrontOfMePosition = cars.higherKey(position);
            }
            
            int myNewPosition = Math.min(carInFrontOfMePosition - 10, position + car.getCurrentSpeed());
            
            
            //If my speed is less than distance to car in front of me + 10 (minimum distance), I'm too slow :(
            

            //updatedMap.put(carPosition, entry.getValue());
        }
        

    }

}
