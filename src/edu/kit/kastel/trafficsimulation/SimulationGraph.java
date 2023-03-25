package edu.kit.kastel.trafficsimulation;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.kit.kastel.trafficsimulation.StreetNodes.Crossing;
import edu.kit.kastel.trafficsimulation.StreetNodes.Roundabout;
import edu.kit.kastel.trafficsimulation.StreetNodes.StreetNode;

/**
 * this class represents the graph of the simulation.
 * It contains all streets, nodes and cars.
 * 
 * @author uxler
 * @version 1.0
 */
public class SimulationGraph {

    /** counter that is increased in order to get a new streetId */ 
    private int streetIdentifierCounter = 0;
    
    /** the HashMap containing all streets mapped to their ids */
    private HashMap<Integer, Street> streetMap = new HashMap<>();

    /** the HashMap containing all nodes (crossings) mapped to their ids */
    private HashMap<Integer, StreetNode> nodeMap = new HashMap<>();

    /** the HashMap containing all cars mapped to their ids */
    private HashMap<Integer, Car> carMap = new HashMap<>();

    /**
     * method to get a given car from the carMap by its id
     * @param id the id of the car
     * @return the car with the given id
     */
    public Car getCarById(int id) {
        return carMap.get(id);
    }

    /**
     * method to get a new id for a street.
     * @return the new id
     */
    private int getNewStreetID() {
        return streetIdentifierCounter++;
    }

    /**
     * method to check if a node with a given id exists
     * @param id the id of the node
     * @return true if the node exists, false otherwise
     */
    public boolean containsNode(int id) {
        return nodeMap.containsKey(id);
    }

    /**
     * method to check if a street with a given id exists
     * @param id the id of the street
     * @return true if the street exists, false otherwise
     */
    public boolean containsStreet(int id) {
        return streetMap.containsKey(id);
    }

    /**
     * method to add a new node to the graph
     * @param id the id of the node
     * @param greenPhaseDuration the duration of the green phase of the node
     */
    public void addNode(int id, int greenPhaseDuration) {

        if (greenPhaseDuration == 0) {
            nodeMap.put(id, new Roundabout(this, id));
        } else if (greenPhaseDuration > 0) {
            nodeMap.put(id, new Crossing(this, id, greenPhaseDuration));
        } else {
            throw new IllegalArgumentException("Error: The greenPhaseDuration of a node cannot be less than 0");
        }

    }

    /**
     * method to add a new street to the graph
     * @param startNode the if of the node the streets starts at
     * @param endNode the id of the node the street ends at
     * @param length the length of the street
     * @param type the type of the street (1 = only one lane, 2 = two lanes)
     * @param maxSpeed the maximum speed allowed on the street
     */
    public void addStreet(int startNode, int endNode, int length, int type, int maxSpeed) {

        int id = getNewStreetID();

        Street street = new Street(id, this, length, type, maxSpeed);

        streetMap.put(id, street);

        nodeMap.get(startNode).addOutputStreet(id);
        nodeMap.get(endNode).addInputStreet(id);

    }

    /**
     * method to get a given street form the streetMap by its id
     * @param id the id of the street
     * @return the street with the given id
     */
    public Street getStreetById(int id) {
        return streetMap.get(id);
    }

    /**
     * method to check if a graph is valid.
     * it just calls isValid() on all streets and nodes
     * @return true if the graph is valid, false otherwise
     */
    public boolean isValid() {
        //check if all streets are valid
        for (Entry<Integer, Street> streetEntry : streetMap.entrySet()) {
            if (!streetEntry.getValue().isValid()) {
                return false;
            }
        }

        //check if all nodes are valid
        for (Entry<Integer, StreetNode> nodeEntry : nodeMap.entrySet()) {
            if (!nodeEntry.getValue().isValid()) {
                return false;
            }
        }

        //if no invalid street or node was found the graph is valid
        return true;
    }

    /**
     * method to check if a car with a given id exists in the carMap
     * @param carID the id of the car
     * @return true if the car exists, false otherwise
     */
    public boolean containsCar(int carID) {
        return carMap.containsKey(carID);
    }

    /**
     * method to check if a street with a given id has space for a 
     * car to enter from the end
     * @param streetID the id of the street
     * @return true if the street has space for a car, false otherwise
     */
    public boolean streetIdHasSpaceForCar(int streetID) {
        return streetMap.get(streetID).hasSpaceForCar();
    }

    /**
     * method to add a car to a street.
     * the car gets added from behind.
     * @param streetID the id of the street
     * @param car the car to add
     */
    public void addCarToStreet(int streetID, Car car) {
        carMap.put(car.getId(), car);
        streetMap.get(streetID).addCar(car.getId());
    }
    
    /**
     * method to get the position on a street
     * of a car with a given id
     * @param carId the id of the car
     * @return the position of the car on the street it is on
     */
    public int getCarPosition(int carId) {
        Car car = carMap.get(carId);
        return car.getPositionOnStreet();
    }

    /**
     * method to get the id of the street a car with a given id is on
     * @param carId the id of the car
     * @return the id of the street the car is on
     */
    public int getCarOnStreetId(int carId) {
        Car car = carMap.get(carId);
        return car.getOnStreetId();
    }

    /**
     * method to get the speed of a car with a given id
     * @param carId the id of the car
     * @return the speed of the car
     */
    public int getCarSpeed(int carId) {
        Car car = carMap.get(carId);
        return car.getSpeed();
    }

    /**
     * method to get the position on a street
     * of a car with a given id
     * @param carId the id of the car
     * @return the position of the car on the street it is on
     */
    public int getCarPositionOnStreet(int carId) {
        Car car = carMap.get(carId);
        return car.getPositionOnStreet();
    }

    /**
     * this method simulates one tick of the simulation.
     * it updates the position of all cars and handles crossing
     */
    public void tick() {
        //set all cars to not crossed this tick
        //and update their speed
        for (Entry<Integer, Car> carEntry : carMap.entrySet()) {
            Car car = carEntry.getValue();
            car.setAlreadyCrossedThisTick(false);
            int maxSpeed = streetMap.get(car.getOnStreetId()).getMaxSpeed();
            car.updateSpeed(maxSpeed);
        }

        //update all car positions
        for (Entry<Integer, Street> streetEntry : streetMap.entrySet()) {
            streetEntry.getValue().updateCarPositions();
        }

        //handle crossing
        for (Entry<Integer, StreetNode> nodeEntry : nodeMap.entrySet()) {
            nodeEntry.getValue().handleCrossing();
        }

        
    }

}
