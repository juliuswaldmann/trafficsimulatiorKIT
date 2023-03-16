package edu.kit.kastel.trafficsimulation.StreetNodes;

public class Crossing extends StreetNode {

    /** This variables stores the duration a traffic light stays green at one street of the 
     * crossing before switching to the next street.*/
    private int greenPhaseDuration;
    
    /** This variable stores which of the connected streets is currently green */
    private int greenPhaseIndicator;

    public Crossing(int nodeID, int greenPhaseDuration) { 
        typeName = "Crossing";

        if(greenPhaseDuration < 3 || greenPhaseDuration > 10) {
            throw new IllegalArgumentException("Error: The greenPhaseDuration has to be at least 3 and at most 10");
        }

        this.nodeID = nodeID;

    }
}
