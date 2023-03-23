package edu.kit.kastel.trafficsimulation.StreetNodes;

import java.util.ArrayList;

import edu.kit.kastel.trafficsimulation.Car;
import edu.kit.kastel.trafficsimulation.SimulationGraph;
import edu.kit.kastel.trafficsimulation.Street;

public abstract class StreetNode {

    public static final int CAR_MINIMUM_DISTANCE = 10; //TODO change this so we only have to specify it once and not in every file

    protected String typeName;

    protected int nodeID;

    /** The graph in which this node exists. */
    protected SimulationGraph parentGraph;

    protected ArrayList<Integer> connectedInputStreets = new ArrayList<>();
    protected ArrayList<Integer> connectedOutputStreets = new ArrayList<>();

    public abstract void handleCrossing();

    public int getID() {
        return nodeID;
    }

    public void addInputStreet(int id) {
        connectedInputStreets.add(id);
    }

    public void addOutputStreet(int id) {
        connectedOutputStreets.add(id);
    }

    public int inputStreetCount() {
        return connectedInputStreets.size();
    }

    public int outputStreetCount() {
        return connectedOutputStreets.size();
    }

    /**
     * let cars from a specific street cross the node. 
     * Note: The caller of this method has to make sure the 
     * street number is a valid street. 
     * If no car can cross a this time it just does nothing.
     * @param inputStreet the street from which the cars are able to cross
     */
    void crossFromStreetCars(int inputStreetId) {
        
        Street inputStreet = parentGraph.getStreetById(inputStreetId);

        Car crossingCar;
        if ((crossingCar = inputStreet.getCrossingCar()) == null) {
            return;
        }
        //if the car has already crossed this tick return immediately
        if (crossingCar.hasAlreadyCrossedThisTick()) {
            return;
        } 

        //this index is guaranteed to be inside the bounds of the "connectedOutputStreets" ArrayList
        int outputStreetIndex = crossingCar.getAndIncreaseWantedDirection() % connectedOutputStreets.size(); //TODO do we need "+1"?


        Street outputStreet = parentGraph.getStreetById(connectedOutputStreets.get(outputStreetIndex));

        Integer lastCarOnTargetStreetPosition;
        if ((lastCarOnTargetStreetPosition = outputStreet.getLastCarPostion()) == null) {
            return;
        }

        if (lastCarOnTargetStreetPosition <= CAR_MINIMUM_DISTANCE) { //TODO do we need "<=" or "<" here?
            return;
        }

        outputStreet.carDrivesIn(crossingCar);
        inputStreet.removeCrossingCar();


    }

}
