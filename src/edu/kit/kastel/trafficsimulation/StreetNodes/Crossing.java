package edu.kit.kastel.trafficsimulation.StreetNodes;

import edu.kit.kastel.trafficsimulation.SimulationGraph;
import edu.kit.kastel.trafficsimulation.Street;

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
     * method to update the green phase of the crossing after every tick
     */
    @Override
    public void tick() {
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

    /**
     * method to check if a car is allowed to cross the roundabout to a certain street
     * @param inputStreetId the id of the street the car is currently on
     * @param wantedDirection the direction the car wants to go
     * @return the street the car is allowed to cross to, or null if the car is not allowed to cross
     */
    @Override
    public Street carIdIsAllowedToCrossToWhichStreet(int inputStreetId, int wantedDirection) {

        if (connectedInputStreets.indexOf(inputStreetId) != greenPhaseIndicator) {
            return null;
        }

        int outputStreetId = connectedOutputStreets.get(wantedDirection % connectedOutputStreets.size());
        Street outputStreet = parentGraph.getStreetById(outputStreetId);

        if (outputStreet.hasSpaceForCar()) {
            return outputStreet;
        } else {
            return null;
        }

    }
}
