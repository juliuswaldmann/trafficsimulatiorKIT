package edu.kit.kastel.trafficsimulation.StreetNodes;

import edu.kit.kastel.trafficsimulation.SimulationGraph;

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

}
