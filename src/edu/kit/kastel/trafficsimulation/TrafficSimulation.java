package edu.kit.kastel.trafficsimulation;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import edu.kit.kastel.trafficsimulation.io.SimulationFileLoader;

public class TrafficSimulation {
    
    static final String STREET_INPUT_FORMAT = "[0-9]+-->[0-9]+:[0-9]+m,[1-2]x,[0-9]+max";
    static final String CROSSING_INPUT_FORMAT = "[0-9]+:[0-9]+t";
    static final String CAR_INPUT_FORMAT = "[0-9]+,[0-9]+,[0-9]+,[0-9]+";
    static final String POSITION_INPUT_FORMAT = "[0-9]+";
    static final String SIMULATE_INPUT_FORMAT = "[0-9]+";

    static final String QUIT_COMMAND = "quit";

    static final String INVALID_COMMAND_ERROR = "Error: \"%s\" is not a valid command";
    static final String INVALID_FILE_LINE_ERROR = "Error: \"%s\" is not a valid line";
    static final String UNKNOWN_NODE_ERROR = "Error: 404 Node \"%s\" not found";
    static final String UNKNOWN_STREET_ERROR = "Error: 404 Street \"%s\" not found";
    static final String CAR_ALREADY_EXISTS_ERROR = "Error: A car with id %s already exists";
    static final String STREET_ALREADY_FULL_ERROR = "Error: Street %s is full";
    static final String GRAPH_NOT_VALID_ERROR = "Error: The graph is not valid";
    static final String NO_GRAPH_LOADED_ERROR = "Error: There is no graph currently loaded. Please load a graph first";
    static final String INVALID_POSITION_INPUT_FORMAT_ERROR = "Error: \"%s\" is not a valid parameter for the position command";
    static final String UNKNOWN_CAR_ERROR = "Error: There is no car with the identifier %s.";
    static final String INVALID_SIMULATE_INPUT_FORMAT_ERROR = "Error: \"%s\" is not a valid parameter for the simulate command. Please enter a positive integer.";

    static final String READY_SUCCESS_MESSAGE = "READY";

    static final String POSITION_OUTPUT_STRING = "Car %s on street %s with speed %s and position %s";

    static final int STREET_MAX_LENGTH = 10000;
    static final int STREET_MIN_LENGTH = 10;

    static final int STREET_MAX_SPEED_LIMIT = 40;
    static final int STREET_MIN_SPEED_LIMIT = 5;

    private Scanner scanner;

    private SimulationGraph loadedGraph;

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        TrafficSimulation simulation = new TrafficSimulation(scanner);

        while (simulation.getCommand()) {}

    }

    public TrafficSimulation(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean getCommand() {
        
        String line = scanner.nextLine();

        

        String[] lineSplits = line.split(" ");

        switch (lineSplits.length) {
            case 1:
                if (lineSplits[0].equals(QUIT_COMMAND)) {
                    return false;
                } 
                System.err.println(String.format(INVALID_COMMAND_ERROR, line));
                break;
            case 2:
                handleCommandWithParamenter(lineSplits);
                break;
            
            default:
                System.err.println(String.format(INVALID_COMMAND_ERROR, line));
                break;
        }

        return true;

    }

    void handleCommandWithParamenter(String[] input) {
        switch (input[0]) {
            case "load":
                handleLoad(input[1]);
                break;
            case "simulate":
                handleSimulate(input[1]);
                break;
            case "position":
                handlePosition(input[1]);
                break;
        }
    }

    void handleLoad(String param) {

        List<String> streetConstructors;
        List<String> crossingConstructors;
        List<String> carConstructors;

        SimulationGraph newGraph = new SimulationGraph();

        try {
            SimulationFileLoader loader = new SimulationFileLoader(param);

            streetConstructors = loader.loadStreets();
            crossingConstructors = loader.loadCrossings();
            carConstructors = loader.loadCars();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }



        //we do the three checks seperate because we want to stop immediately if one of them fails

        boolean crossingStatus = loadCrossings(crossingConstructors, newGraph);
        if (!crossingStatus) {
            return; // already printed the error message so we can just return
        }

        boolean streetStatus = loadStreets(streetConstructors, newGraph);
        if (!streetStatus) {
            return; // already printed the error message so we can just return
        }

        boolean carStatus = loadCars(carConstructors, newGraph);
        if (!carStatus) {
            return; // already printed the error message so we can just return
        }

        

        //check for validity of graph
        if (!newGraph.isValid()) {
            System.err.println(GRAPH_NOT_VALID_ERROR);
            return;
        }

        //if we get here everything is valid and we can set the new graph

        loadedGraph = newGraph;

        System.out.println(READY_SUCCESS_MESSAGE);

    }

    boolean loadCrossings(List<String> crossingConstructors, SimulationGraph newGraph) {
        //crossings
        for (String string : crossingConstructors) {
            if (!string.matches(CROSSING_INPUT_FORMAT)) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }
            
            String[] subStrings = string.split(":|t");

            int nodeId = Integer.parseInt(subStrings[0]);
            int greenPhaseDuration = Integer.parseInt(subStrings[1]);

            if (nodeId < 0 || greenPhaseDuration < 0) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }

            newGraph.addNode(nodeId, greenPhaseDuration);

        }

        return true;
    }

    boolean loadStreets(List<String> streetConstructors, SimulationGraph newGraph) {
        //streets.sim
        for (String string : streetConstructors) {
            if (!string.matches(STREET_INPUT_FORMAT)) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }
            //we know the string is valid so we can just extract the numbers

            String[] subStrings = string.split("-->|:|m,|x,|max");
            
            //we now have all numbers as seperate strings in order.
            //the "Integer.parseInt" calls will never error because 
            //we already checked their validity earlier.
            int startingNodeId = Integer.parseInt(subStrings[0]);
            int endNodeId = Integer.parseInt(subStrings[1]);
            int streetLength = Integer.parseInt(subStrings[2]);
            int streetType = Integer.parseInt(subStrings[3]);
            int speedLimit = Integer.parseInt(subStrings[4]);

            //now check the validity of these numbers
            if (
                startingNodeId < 0 
                || endNodeId < 0 
                || streetLength < STREET_MIN_LENGTH 
                || streetLength > STREET_MAX_LENGTH 
                || speedLimit < STREET_MIN_SPEED_LIMIT 
                || speedLimit > STREET_MAX_SPEED_LIMIT
                || !(streetType == 1 || streetType == 2)
            ) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }

            //check if both nodes exist
            if (!newGraph.containsNode(startingNodeId)) {
                System.err.println(String.format(UNKNOWN_NODE_ERROR, startingNodeId));
                return false;
            }
            if (!newGraph.containsNode(endNodeId)) {
                System.err.println(String.format(UNKNOWN_NODE_ERROR, endNodeId));
                return false;
            }

            newGraph.addStreet(startingNodeId, endNodeId, streetLength, streetType, speedLimit);

        }

        return true;
    }

    boolean loadCars(List<String> carConstructors, SimulationGraph newGraph) {
        //cars.sim
        for (String string : carConstructors) {
            if (!string.matches(CAR_INPUT_FORMAT)) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }

            String[] subStrings = string.split(",");

            int carId = Integer.parseInt(subStrings[0]);
            int startingStreetId = Integer.parseInt(subStrings[1]);
            int wantedSpeed = Integer.parseInt(subStrings[2]);
            int acceleration = Integer.parseInt(subStrings[3]);

            if (
                carId < 0 
                || startingStreetId < 0 
                || wantedSpeed < 20
                || wantedSpeed > 40
                || acceleration < 1
                || acceleration > 10
            ) {
                System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                return false;
            }

            if (newGraph.containsCar(carId)) {
                System.err.println(String.format(CAR_ALREADY_EXISTS_ERROR, carId));
                return false;
            }

            if (!newGraph.containsStreet(startingStreetId)) {
                System.err.println(String.format(UNKNOWN_STREET_ERROR, startingStreetId));
                return false;
            }

            if (!newGraph.streetIdHasSpaceForCar(startingStreetId)) {
                System.err.println(String.format(STREET_ALREADY_FULL_ERROR, startingStreetId));
                return false;
            }

            Car car = new Car(carId, acceleration, wantedSpeed);

            newGraph.addCarToStreet(startingStreetId, car);

        }

        return true;
    }


    void handlePosition(String input) {
        if (loadedGraph == null) {
            System.err.println(NO_GRAPH_LOADED_ERROR);
            return;
        }

        if (!input.matches(POSITION_INPUT_FORMAT)) {
            System.err.println(String.format(INVALID_POSITION_INPUT_FORMAT_ERROR, input));
            return;
        }

        //this will not error because we already checked the format of "input" to be a valid (positive) integer
        int carId = Integer.parseInt(input);

        if (!loadedGraph.containsCar(carId)) {
            System.err.println(String.format(UNKNOWN_CAR_ERROR, carId));
            return;
        }

        int onStreetID = loadedGraph.getCarOnStreetId(carId);
        int speed = loadedGraph.getCarSpeed(carId);
        int position = loadedGraph.getCarPosition(carId);


        System.out.println(String.format(POSITION_OUTPUT_STRING, carId, onStreetID, speed, position));

    }

    void handleSimulate(String input) {

        if (loadedGraph == null) {
            System.err.println(NO_GRAPH_LOADED_ERROR);
            return;
        }

        if (!input.matches(SIMULATE_INPUT_FORMAT)) {
            System.err.println(String.format(INVALID_SIMULATE_INPUT_FORMAT_ERROR, input));
            return;
        }

        //this will not error because we already checked the format of "input" to be a valid integer
        //it is also guaranteed to be positive because the regex only allows for positive integers
        int ticksToSimulate = Integer.parseInt(input);

        for (int i = 0; i < ticksToSimulate; i++) {
            loadedGraph.tick();
        }

        System.out.println(READY_SUCCESS_MESSAGE);

    }
}
