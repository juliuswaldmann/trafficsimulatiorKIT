package edu.kit.kastel.trafficsimulation.StreetNodes;

import java.util.ArrayList;

import edu.kit.kastel.trafficsimulation.SimulationGraph;

import edu.kit.kastel.trafficsimulation.Street;

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

    /**
     * method to get the street that a car with a specific id is allowed to cross to.
     * @param streetId the id of the street the car is currently on
     * @param wantedDirection the direction the car wants to go
     * @return the street the car is allowed to cross to, null if the car is not allowed to cross
     */
    public abstract Street carIdIsAllowedToCrossToWhichStreet(int streetId, int wantedDirection);

    /**
     * method that should be called every tick
     */
    public abstract void tick();

}
