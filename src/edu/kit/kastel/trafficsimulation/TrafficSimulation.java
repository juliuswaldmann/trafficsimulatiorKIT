package edu.kit.kastel.trafficsimulation;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import edu.kit.kastel.trafficsimulation.io.SimulationFileLoader;

/**
 * This class is the main class of the traffic simulation.
 * It handles the input from the user and calls the appropriate methods.
 * It also handles the loading of the simulation files.
 * 
 * @author uxler
 * @version 1.0
 */
public class TrafficSimulation {

    /** the minimum distance a car must keep to the next car */
    public static final int CAR_MINIMUM_DISTANCE = 10;


    /** the input format of a street in the "streets.sim" file */
    static final String STREET_INPUT_FORMAT = "[0-9]+-->[0-9]+:[0-9]+m,[1-2]x,[0-9]+max";
    /** the input format of a crossing in the "crossings.sim" file */
    static final String CROSSING_INPUT_FORMAT = "[0-9]+:[0-9]+t";
    /** the input format of a car in the "cars.sim" file */
    static final String CAR_INPUT_FORMAT = "[0-9]+,[0-9]+,[0-9]+,[0-9]+";
    
    /** the input format for the position command */
    static final String POSITION_INPUT_FORMAT = "[0-9]+";
    /** the input format for the simulate command */
    static final String SIMULATE_INPUT_FORMAT = "[0-9]+";

    /** the string for the load command */
    static final String QUIT_COMMAND = "quit";

    /** the error printed when a command is invalid */
    static final String INVALID_COMMAND_ERROR = "Error: \"%s\" is not a valid command";
    /** the error printed when an invalid line is in one of the .sim files */
    static final String INVALID_FILE_LINE_ERROR = "Error: \"%s\" is not a valid line";
    /** the error printed when a street in the .sim file is supposed to be connected to an node that does not exist */
    static final String UNKNOWN_NODE_ERROR = "Error: 404 Node \"%s\" not found";
    /** the error printed when a car in the .sim file is supposed to be added to an street that does not exist */
    static final String UNKNOWN_STREET_ERROR = "Error: 404 Street \"%s\" not found";
    /** the error given when a car with a given id already exists */
    static final String CAR_ALREADY_EXISTS_ERROR = "Error: A car with id %s already exists";
    /** the error given when there is no more space for a car on a street */
    static final String STREET_ALREADY_FULL_ERROR = "Error: Street %s is full";
    /** the error given when a graph is not valid */
    static final String GRAPH_NOT_VALID_ERROR = "Error: The graph is not valid";
    /** the error given when a user tries to use a command but there is no graph loaded */
    static final String NO_GRAPH_LOADED_ERROR = "Error: There is no graph currently loaded. Please load a graph first";
    /** the error given if the input for the position command is not a valid it */
    static final String INVALID_POSITION_INPUT_FORMAT_ERROR = "Error: \"%s\" invalid parameter for position";
    /** error given when the user tries to get the position of a car that does not exist */
    static final String UNKNOWN_CAR_ERROR = "Error: There is no car with the identifier %s.";
    /** the error given if the input for the simulate command is not a valid it */
    static final String INVALID_SIMULATE_INPUT_FORMAT_ERROR = "Error: \"%s\" invalid parameter. Enter positive integer";

    /** the message printed when the graph is loaded successfully */
    static final String READY_SUCCESS_MESSAGE = "READY";

    /** the output string for the position command */
    static final String POSITION_OUTPUT_STRING = "Car %s on street %s with speed %s and position %s";

    /** the max length a street can have */
    static final int STREET_MAX_LENGTH = 10000;
    /** the minimum length a street must have */
    static final int STREET_MIN_LENGTH = 10;

    /** the max speed limit a street can have */
    static final int STREET_MAX_SPEED_LIMIT = 40;
    /** the minimum speed limit a street must have */
    static final int STREET_MIN_SPEED_LIMIT = 5;

    /** The minimum speed a car wants to have */
    static final int MIN_WANTED_SPEED = 20;

    /** The maximum speed a car wants to have */
    static final int MAX_WANTED_SPEED = 40;

    /** The minimum acceleration a car can have */
    static final int MIN_ACCELERATION = 1;

    /** The maximum acceleration a car can have */
    static final int MAX_ACCELERATION = 10;

    /** the scanner object used by the class to get input from the command line*/
    private Scanner scanner;

    /** the graph that is currently loaded */
    private SimulationGraph loadedGraph;

    /** 
     * the constructor of the class
     * @param scanner the scanner object used by the class to get input from the command line
     */
    public TrafficSimulation(Scanner scanner) {
        this.scanner = scanner;
    }

    /** 
     * the main method of the program
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        TrafficSimulation simulation = new TrafficSimulation(scanner);

        boolean simulationRunning = true;
        while (simulationRunning) {
            simulationRunning = simulation.getCommand();
        }
    }
    
    /**
     * Reads the next command from the scanner and executes it.
     * @return true if the program should continue, false if it should quit
     */
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

    /**
     * handles all commands that accept a parameter
     * @param input the input string split by spaces
     */
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
            default:
                System.err.println(String.format(INVALID_COMMAND_ERROR, input[0]));
                break;
        }
    }

    /**
     * this method handles all calls of the load commands
     * @param param the parameter that was given after the load command
     */
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

    /**
     * this method loads all crossings and roundabouts from the "crossings.sim" file and 
     * adds them to the graph after checking if they are valid
     * @param crossingConstructors a list of strings that contain the information about the crossings
     * @param newGraph the graph that the crossings should be added to
     * @return true if the crossings were loaded successfully, false if there was an error
     */
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

    /**
     * this method loads all streets from the "streets.sim" file and 
     * adds them to the graph after checking if they are valid
     * @param streetConstructors a list of strings that contain the information about the streets
     * @param newGraph the graph that the streets should be added to
     * @return true if the streets were loaded successfully, false if there was an error
     */
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

            boolean streetLengthIsNotValid = (streetLength < STREET_MIN_LENGTH || streetLength > STREET_MAX_LENGTH);
            boolean speedLimitIsNotValid = (speedLimit < STREET_MIN_SPEED_LIMIT || speedLimit > STREET_MAX_SPEED_LIMIT);
            boolean streetTypeIsNotValid = (streetType != 1 && streetType != 2);

            if (
                    startingNodeId < 0 
                    || endNodeId < 0 
                    || streetLengthIsNotValid
                    || speedLimitIsNotValid
                    || streetTypeIsNotValid
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

    /**
     * this method loads all cars from the "cars.sim" file and 
     * adds them to the graph after checking if they are valid
     * @param carConstructors a list of strings that contain the information about the cars
     * @param newGraph the graph that the cars should be added to
     * @return true if the cars were loaded successfully, false if there was an error
     */
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
                    || wantedSpeed < MIN_WANTED_SPEED
                    || wantedSpeed > MAX_WANTED_SPEED
                    || acceleration < MIN_ACCELERATION
                    || acceleration > MAX_ACCELERATION
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

    /**
     * method to handle the "position" command
     * prints the position of the car with the given id
     * @param input the input string that contains the id of the car
     */
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

    /**
     * method to handle the "simulate" command
     * simulates a given number of ticks
     * @param input the input string that contains the number of ticks to simulate
     */
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
