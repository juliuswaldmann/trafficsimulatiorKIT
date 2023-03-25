package edu.kit.kastel.trafficsimulation.StreetNodes;

import edu.kit.kastel.trafficsimulation.SimulationGraph;

/**
 * this class represents a crossing in the simulation.
 * It is a subclass of StreetNode.
 * 
 * @author uxler
 * @version 1.0
 */
public class Crossing extends StreetNode {

    /** This variables stores the duration a traffic light stays green at one street of the 
     * crossing before switching to the next street.*/
    private int greenPhaseDuration;
    
    /** This variable stores which of the connected streets is currently green */
    private int greenPhaseIndicator;

    /** the timer that counts the length of the current green phase */
    private int greenPhaseTimer;

    /**
     * constructor for a new crossing
     * @param parentGraph the graph in which the crossing exists
     * @param nodeID the id of the crossing
     * @param greenPhaseDuration the duration of the green phase of the crossing
     */
    public Crossing(SimulationGraph parentGraph, int nodeID, int greenPhaseDuration) { 
        this.nodeID = nodeID;
        this.parentGraph = parentGraph;
    }

    /**
     * method to handle the crossing of cars at this node from and to the streets.
     * This method is called by the simulation every time step.
     */
    @Override
    public void handleCrossing() {

        crossFromStreetCars(greenPhaseIndicator);

        updateGreenPhase();        

    }

    /**
     * method to update the green phase of the crossing
     * This method is called by the simulation every time step.
     * It switches the green phase to the next street after the green phase duration has passed.
     */
    private void updateGreenPhase() {
        greenPhaseTimer--;
        if (greenPhaseTimer != 0) {
            return;
        }

        greenPhaseTimer = greenPhaseDuration;

        greenPhaseIndicator++;
        if (greenPhaseIndicator >= connectedInputStreets.size()) {
            greenPhaseIndicator = 0;
        }
    }
}
