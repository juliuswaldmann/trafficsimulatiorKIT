package edu.kit.kastel.trafficsimulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class represents a street in the simulation.
 * It contains all the information about the street and which cars are where on it.
 * 
 * @author uxler
 * @version 1.0
 */
public class Street {

    /** The length of the street in meters (min 5, max 40)*/
    private int length;
    
    /** The max speed allowed on the street in m/tick (min 5, max 40) */
    private int maxSpeed;

    /** If the street has two lanes or not and if cars can therefore overtake each other */
    private boolean overtakeable;

    /** The parent graph of this street */
    private SimulationGraph parentGraph;

    /** The id of this street */
    private int id;

    /**
     * This Hashmap stores all cars that are currently on a street.
     * The Key in this map is the current distance the cars has to the EndNode.
     * The reason for it being a HashMap is that the task explicitly states
     * that cars are updated in order (with the last car coming first).
     * Additionally we know that there are never two cars at one spot at 
     * the same time because it is a given that cars have to keep a 10m distance 
     * to each other. 
     * with the lowest key (car with the lowest distance to the EndNode).
     * The key is the distance to the EndNode. The value is the carId.
     */
    private NavigableMap<Integer, Integer> cars = new TreeMap<>();


    /**
     * This constructor creates a new street with the given parameters.
     * @param id the id of the street
     * @param parentGraph the parent graph of the street
     * @param length the length of the street in meters
     * @param type the type of the street (1 lane or 2 lanes)
     * @param maxSpeed the max speed allowed on the street in m/tick
     */
    public Street(int id, SimulationGraph parentGraph, int length, int type, int maxSpeed) { 
        this.length = length;
        this.maxSpeed = maxSpeed;

        // parse the lane count to a boolean whether the street is overtakeable or not
        if (type == 1) {
            this.overtakeable = false;
        } else if (type == 2) {
            this.overtakeable = true;
        } else {
            throw new IllegalArgumentException("Error: the type of a street has to be 1 or 2");
        }

        this.parentGraph = parentGraph;
        this.id = id;
        
    }

    /**
     * Returns the max speed allowed on the street in m/tick
     * @return the max speed allowed on the street in m/tick
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Updates the position of all cars on the street.
     * This method is called every tick.
     */
    public void updateCarPositions() {

        ArrayList<Integer> keySet = new ArrayList<Integer>(cars.keySet());
        Collections.sort(keySet);

        NavigableMap<Integer, Integer> updatedMap = new TreeMap<>();

        
        Entry<Integer, Integer> entry;
        while ((entry = cars.pollFirstEntry()) != null) { //cycle through all cars in order

            int initialPosition = entry.getKey();
            Car car = parentGraph.getCarById(entry.getValue());

            boolean noNextCar = false;
            Integer nextCarPosition = updatedMap.higherKey(initialPosition); //has to be "Integer" so it can be "null"

            if (nextCarPosition == null) {
                //the car in front of the current car overtook the current car
                nextCarPosition = cars.higherKey(initialPosition);
                if (nextCarPosition == null) {
                    //the current car is the farthest car on the street
                    nextCarPosition = length; 
                    noNextCar = true;
                    //if there is no car in front we set the nextCarPosition to the street length
                    //if this is the case secondNextCarPosition will also be the street length and
                    //the rest of the code will work perfectly
                }
            }

            Integer secondNextCarPosition = updatedMap.higherKey(nextCarPosition); 
            if (secondNextCarPosition == null) {
                secondNextCarPosition = cars.higherKey(nextCarPosition);
                if (secondNextCarPosition == null) {
                    secondNextCarPosition = length;
                    noNextCar = true;
                }
            }

            int newPosition;
            int metersLeftAfterNextCar = secondNextCarPosition - nextCarPosition;
            boolean enoughSpaceAfter = metersLeftAfterNextCar >= TrafficSimulation.CAR_MINIMUM_DISTANCE * 2;
            int speedNeeded = nextCarPosition - initialPosition + TrafficSimulation.CAR_MINIMUM_DISTANCE;
            boolean fastEnough = car.getCurrentSpeed() >= speedNeeded;
            int furthestPosBySpeed = initialPosition + car.getCurrentSpeed();
            if (overtakeable && enoughSpaceAfter && fastEnough) {
                int furthestPosByCarDistance = secondNextCarPosition - TrafficSimulation.CAR_MINIMUM_DISTANCE;
                newPosition = Math.min(furthestPosBySpeed, furthestPosByCarDistance);
                car.setAlreadyCrossedThisTick(true); //if a car has overtook another, it cannot cross in the same tick!
            } else if (noNextCar) {
                newPosition = Math.min(furthestPosBySpeed, length);
            } else {
                newPosition = Math.min(furthestPosBySpeed, nextCarPosition - TrafficSimulation.CAR_MINIMUM_DISTANCE);
            }


            //Set speed to 0 if it's stuck behind a slow driver
            if (
                    nextCarPosition - initialPosition == TrafficSimulation.CAR_MINIMUM_DISTANCE 
                    && !noNextCar && !(overtakeable && enoughSpaceAfter && fastEnough)
            ) {
                car.setSpeed(0);
            }



            car.droveMeters(newPosition - initialPosition);
            car.setPositionOnStreet(newPosition);

            updatedMap.put(newPosition, car.getId());

        }

        cars = updatedMap;

    }

    /**
     * This method gets the Car that is currently at the
     * end of the street and awaits to cross. It also removes 
     * the car from the street.
     * @return the car that is at the end of the street. 
     *         If there is none or if the car has no distance 
     *         left to drive it returns null
     */
    public Car getCrossingCar() {
        Entry<Integer, Integer> lastEntry = cars.lastEntry();

        if (lastEntry == null) {
            return null;
        }

        if (parentGraph.getCarById(lastEntry.getValue()).getMetersLeftToDrive() == 0) {
            return null;
        }

        return parentGraph.getCarById(lastEntry.getValue());

    }

    /**
     * Removes the car that is currently at the
     * end of the street and awaits to cross.
     */
    public void removeCrossingCar() {
        Entry<Integer, Integer> lastEntry = cars.lastEntry();
        cars.remove(lastEntry.getKey());
    }

    /**
     * Method to get the position of the last car on the street
     * @return the position of the last car on the street
     */
    public Integer getLastCarPostion() {
        if (cars.isEmpty()) {
            return null;
        }

        return cars.firstEntry().getKey();
    }

    /**
     * Handles a car driving in on the street
     * @param car the car that drives in
     */
    public void carDrivesIn(Car car) {
        int maxNewPosition;
        Integer lastCarPosition = getLastCarPostion();

        // check if there is no car on the street
        if (lastCarPosition == null) {
            // then the street is entirely available for the car
            lastCarPosition = length;
            maxNewPosition = length;
        } else {
            // if there is a car on the street, the new car has to be at least CAR_MINIMUM_DISTANCE meters away from it
            maxNewPosition = lastCarPosition - TrafficSimulation.CAR_MINIMUM_DISTANCE;
        }

        // the car can only drive as far as it has left to drive and as far as the street allows
        int newPosition = Math.min(car.getMetersLeftToDrive(), maxNewPosition);
        car.droveMeters(newPosition);
        car.setPositionOnStreet(newPosition);
        car.setOnStreetId(this.id);

        cars.put(newPosition, car.getId());

        car.setAlreadyCrossedThisTick(true);

    }

    /**
     * Checks if the street is valid. A street is valid if there are no cars that are too close to each other
     * If there are more cars than physically possible on the street, it is not neccessary to iterate through the cars
     * @return true if the street is valid, false if not
     */
    public boolean isValid() {

        if (!(cars.size() <= length / TrafficSimulation.CAR_MINIMUM_DISTANCE + 1)) {
            return false; //if there are more cars than allowed the street is not valid
        }

        for (Entry<Integer, Integer> carEntry : cars.entrySet()) {
            Integer nextCarPosition = cars.higherKey(carEntry.getKey());
            boolean hasEnoughSpace;
            if (nextCarPosition == null) {
                hasEnoughSpace = true;
            } else {
                hasEnoughSpace = nextCarPosition - carEntry.getKey() <= TrafficSimulation.CAR_MINIMUM_DISTANCE;
            }
            if (nextCarPosition != null && !hasEnoughSpace) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Checks if the street contains the car with the given id
     * @param carId the id of the car to check for
     * @return true if the street contains a car with the given id, false if not
     */
    public boolean containsCar(int carId) {
        for (Entry<Integer, Integer> carEntry : cars.entrySet()) {
            if (parentGraph.getCarById(carEntry.getValue()).getId() == carId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the street has space for a car
     * @return true if the street has space for a car, false if not
     */
    public boolean hasSpaceForCar() {
        if (cars.isEmpty()) {
            return true;
        }
        return getLastCarPostion() >= TrafficSimulation.CAR_MINIMUM_DISTANCE; 
    }

    /**
     * Adds a car to the street with the given id
     * @param carId the id of the car to add
     */
    public void addCar(int carId) {

        int newPosition;

        Integer lastCarPosition = getLastCarPostion();
        if (lastCarPosition == null) {
            newPosition = length;
        } else {
            newPosition = lastCarPosition - TrafficSimulation.CAR_MINIMUM_DISTANCE;
        }

        Car car = parentGraph.getCarById(carId);
        car.setPositionOnStreet(newPosition);
        car.setOnStreetId(this.id);

        cars.put(newPosition, carId);
    }

}
