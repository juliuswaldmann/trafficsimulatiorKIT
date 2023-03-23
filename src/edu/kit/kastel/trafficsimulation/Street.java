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

    public static final int CAR_MINIMUM_DISTANCE = 10;

    /** The length of the street in meters (min 5, max 40)*/
    private int length;
    
    /** The max speed allowed on the street in m/tick (min 5, max 40) */
    private int maxSpeed;

    private int type;

    private boolean overtakeable;

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

    public void updateCarSpeeds() {
        for (Entry<Integer, Car> entry : cars.entrySet()) {
            Car car = entry.getValue();
            car.updateSpeed(maxSpeed);
        }
    }

    /**
     * This method cycles through every car on the street 
     * from the last one to the first one and calculates
     * the new position of the car after overtaking etc //TODO rewrite this
     */
    public void updateCarPositions() {

        ArrayList<Integer> keySet = new ArrayList<Integer>(cars.keySet());
        Collections.sort(keySet);

        NavigableMap<Integer, Car> updatedMap = new TreeMap<>();

        
        Entry<Integer, Car> entry;
        while ((entry = cars.pollFirstEntry()) != null) { //cycle through all cars in order

            int initialPosition = entry.getKey();
            Car car = entry.getValue();

            Integer nextCarPosition; //has to be "Integer" so it can be "null"
            if ((nextCarPosition = updatedMap.higherKey(initialPosition)) != null) {
                //the car in front of the current car overtook the current car
            } else if ((nextCarPosition = cars.higherKey(initialPosition)) != null) {
                //the car in front of the current car did not overtake the current car
            } else {
                //the current car is the farthest car on the street
                nextCarPosition = length; 
                //if there is no car in front we set the nextCarPosition to the street length
                //if this is the case secondNextCarPosition will also be the street length and
                //the rest of the code will work perfectly
            }

            Integer secondNextCarPosition; //Sorry I have no idea how to call this //TODO change this
            if ((secondNextCarPosition = updatedMap.higherKey(nextCarPosition)) != null) {
                
            } else if ((secondNextCarPosition = cars.higherKey(nextCarPosition)) != null) {

            } else {
                secondNextCarPosition = length;
            }

            int newPosition;
            if (
                overtakeable == true 
                && (secondNextCarPosition - nextCarPosition) >= (CAR_MINIMUM_DISTANCE * 2) 
                && car.getCurrentSpeed() >= (nextCarPosition - initialPosition + CAR_MINIMUM_DISTANCE)
            ) {
                newPosition = Math.min(car.getCurrentSpeed() + initialPosition, secondNextCarPosition - CAR_MINIMUM_DISTANCE);
                car.setAlreadyCrossedThisTick(true); //if a car has overtook another, it cannot cross in the same tick!
            } else {
                newPosition = Math.min(initialPosition + car.getCurrentSpeed(), nextCarPosition - CAR_MINIMUM_DISTANCE);
            }

            car.droveMeters(newPosition - initialPosition);
            updatedMap.put(newPosition, car);

        }

        cars = updatedMap;

    }

    /**
     * This method gets the Car that is currently at the
     * end of the street and awaits to cross. It also removes 
     * the car from the street.
     * @return the cat that is at the end of the street. 
     *         If there is none or if the car has no distance 
     *         left to drive it returns null
     */
    public Car getCrossingCar() {
        Entry<Integer, Car> lastEntry = cars.lastEntry();

        if (lastEntry == null) {
            return null;
        }

        if (lastEntry.getValue().getMetersLeftToDrive() == 0) {
            return null;
        }

        return lastEntry.getValue();

    }

    public void removeCrossingCar() {
        Entry<Integer, Car> lastEntry = cars.lastEntry();
        cars.remove(lastEntry.getKey());
    }

    public Integer getLastCarPostion() {
        return cars.firstEntry().getKey();
    }

    public void carDrivesIn(Car car) {

        int newPosition = Math.min(car.getMetersLeftToDrive(), getLastCarPostion() - 10);
        car.droveMeters(newPosition);

        cars.put(newPosition, car);

        car.setAlreadyCrossedThisTick(true);

    }

    public boolean isValid() {
        for (Entry<Integer, Car> carEntry : cars.entrySet()) {
            Integer nextCarPosition = cars.higherKey(carEntry.getKey());
            if (nextCarPosition == null && length - carEntry.getKey() < CAR_MINIMUM_DISTANCE) {
                return false;
            }
            if (nextCarPosition - carEntry.getKey() < CAR_MINIMUM_DISTANCE) {
                return false;
            }
        }
        
        return true;
    }

    public void initTick() {
        for (Entry<Integer, Car> carEntry : cars.entrySet()) {
            carEntry.getValue().setAlreadyCrossedThisTick(false);
        }
    }

}
