package edu.kit.kastel.trafficsimulation.StreetNodes;

import edu.kit.kastel.trafficsimulation.SimulationGraph;
import edu.kit.kastel.trafficsimulation.Street;

/**
 * this class represents a roundabout in the simulation.
 * It is a subclass of StreetNode.
 * 
 * @author uxler
 * @version 1.0
 */
public class Roundabout extends StreetNode {
    
    /**
     * constructor for a new roundabout
     * @param parentGraph the graph in which the roundabout exists
     * @param nodeID the id of the roundabout
     */
    public Roundabout(SimulationGraph parentGraph, int nodeID) {
        this.nodeID = nodeID;
        this.parentGraph = parentGraph;
    }

    /**
     * method to handle the crossing of cars at this node from and to the streets.
     * This method is called by the simulation every time step.
     */
    @Override
    public void handleCrossing() {
        for (Integer streetId : connectedInputStreets) {
            crossFromStreetCars(streetId);
        }
    }

    /**
     * method to check if a car is allowed to cross the roundabout to a certain street
     * @param streetId the id of the street the car is currently on
     * @param wantedDirection the direction the car wants to go
     * @return the street the car is allowed to cross to, or null if the car is not allowed to cross
     */
    @Override
    public Street carIdIsAllowedToCrossToWhichStreet(int streetId, int wantedDirection) {
        int outputStreetId = connectedOutputStreets.get(wantedDirection % connectedOutputStreets.size());
        Street outputStreet = parentGraph.getStreetById(outputStreetId);

        if (outputStreet.hasSpaceForCar()) {
            return outputStreet;
        } else {
            return null;
        }
    }

    /**
     * method to update the roundabout each tick. The roundabout does not need to be updated.
     * Override of the method in StreetNode because crossing is handled differently.
     */
    @Override
    public void tick() {
        //do nothing. this is intended
        
    }

}
