package edu.kit.kastel.trafficsimulation.StreetNodes;

import java.util.ArrayList;

import edu.kit.kastel.trafficsimulation.Car;
import edu.kit.kastel.trafficsimulation.SimulationGraph;
import edu.kit.kastel.trafficsimulation.Street;
import edu.kit.kastel.trafficsimulation.TrafficSimulation;

/**
 * this class represents a node in the simulation.
 * It is a superclass of all nodes.
 * 
 * @author uxler
 * @version 1.0
 */
public abstract class StreetNode {

    /** the id of the node */
    protected int nodeID;

    /** The graph in which this node exists. */
    protected SimulationGraph parentGraph;

    /** every street (by its id) that has this node as a end node */
    protected ArrayList<Integer> connectedInputStreets = new ArrayList<>();
    /** every street (by its id) that has this node as a start node */
    protected ArrayList<Integer> connectedOutputStreets = new ArrayList<>();

    /** 
     * this method is implemented by the child classes.
     * It handles the crossing of cars at this node from and to the streets.
     */
    public abstract void handleCrossing();

    /**
     * method to get the id of the node
     * @return the id of the node
     */
    public int getID() {
        return nodeID;
    }

    /**
     * method to add a new input street to the node
     * @param id the id of the street
     */
    public void addInputStreet(int id) {
        connectedInputStreets.add(id);
    }

    /**
     * method to add a new output street to the node
     * @param id the id of the street
     */
    public void addOutputStreet(int id) {
        connectedOutputStreets.add(id);
    }

    /**
     * method to get the number of input streets
     * @return the number of input streets
     */
    public int inputStreetCount() {
        return connectedInputStreets.size();
    }

    /**
     * method to get the number of output streets
     * @return the number of output streets
     */
    public int outputStreetCount() {
        return connectedOutputStreets.size();
    }

    /**
     * let cars from a specific street cross the node. 
     * Note: The caller of this method has to make sure the 
     * street number is a valid street. 
     * If no car can cross a this time it just does nothing.
     * @param inputStreetId the street from which the cars are able to cross
     */
    void crossFromStreetCars(int inputStreetId) {
        
        Street inputStreet = parentGraph.getStreetById(inputStreetId);

        Car crossingCar = inputStreet.getCrossingCar();
        if (crossingCar == null) {
            return;
        }
        //if the car has already crossed this tick return immediately
        if (crossingCar.hasAlreadyCrossedThisTick()) {
            return;
        } 

        //this index is guaranteed to be inside the bounds of the "connectedOutputStreets" ArrayList
        int outputStreetIndex = (crossingCar.getAndIncreaseWantedDirection() + 1) % connectedOutputStreets.size();


        Street outputStreet = parentGraph.getStreetById(connectedOutputStreets.get(outputStreetIndex));

        Integer lastCarOnTargetStreetPosition = outputStreet.getLastCarPostion();
        if (
                lastCarOnTargetStreetPosition != null 
                && lastCarOnTargetStreetPosition < TrafficSimulation.CAR_MINIMUM_DISTANCE
            ) {
            return;
        }

        outputStreet.carDrivesIn(crossingCar);
        inputStreet.removeCrossingCar();


    }

    /**
     * method to check if the node is valid.
     * A node is valid if it has at least one input and one output street
     * and at most four input and output streets.
     * @return true if the node is valid, false otherwise
     */
    public boolean isValid() {
        if (
                connectedInputStreets.size() < 1
                || connectedOutputStreets.size() < 1
                || connectedInputStreets.size() > 4
                || connectedOutputStreets.size() > 4
        ) {
            return false;
        }

        return true;
    }

}
