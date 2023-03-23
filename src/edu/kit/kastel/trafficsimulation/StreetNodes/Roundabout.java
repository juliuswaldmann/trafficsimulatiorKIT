package edu.kit.kastel.trafficsimulation.StreetNodes;

import java.util.logging.Handler;

import edu.kit.kastel.trafficsimulation.SimulationGraph;

public class Roundabout extends StreetNode {
    
    public Roundabout(SimulationGraph parentGraph,int nodeID) {
        typeName = "Roundabout";
        this.nodeID = nodeID;
        this.parentGraph = parentGraph;
    }

    public void handleCrossing() {
        for (Integer streetId : connectedInputStreets) {
            crossFromStreetCars(streetId);
        }
    }

}
