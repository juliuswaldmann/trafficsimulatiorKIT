package edu.kit.kastel.trafficsimulation.StreetNodes;

import edu.kit.kastel.trafficsimulation.SimulationGraph;

public class Crossing extends StreetNode {

    /** This variables stores the duration a traffic light stays green at one street of the 
     * crossing before switching to the next street.*/
    private int greenPhaseDuration;
    
    /** This variable stores which of the connected streets is currently green */
    private int greenPhaseIndicator;

    private int greenPhaseTimer;

    public Crossing(SimulationGraph parentGraph, int nodeID, int greenPhaseDuration) { 
        typeName = "Crossing";

        if(greenPhaseDuration < 3 || greenPhaseDuration > 10) {
            throw new IllegalArgumentException("Error: The greenPhaseDuration has to be at least 3 and at most 10");
        }

        this.nodeID = nodeID;
        this.parentGraph = parentGraph;
    }

    /**
     * This method handles crossing while respecting greenPhaseIndicator.
     * It also updates the greenPhase accordingly.
     */
    public void handleCrossing() {

        crossFromStreetCars(greenPhaseIndicator);

        updateGreenPhase();        

    }

    private void updateGreenPhase() {
        greenPhaseTimer--;
        if (greenPhaseTimer != 0) {
            return;
        }

        greenPhaseTimer = greenPhaseDuration;

        greenPhaseIndicator++;
        if (greenPhaseIndicator >= connectedInputStreets.size()){
            greenPhaseIndicator = 0;
        }
    }
}
