package edu.kit.kastel.trafficsimulation;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import edu.kit.kastel.trafficsimulation.io.SimulationFileLoader;

public class TrafficSimulation {
    
    static final String STREET_INPUT_FORMAT = "[0-9]+-->[0-9]+:[0-9]+m,[1-2]x,[0-9]+max";
    static final String CROSSING_INPUT_FORMAT = "[0-9]+:[0-9]+t";
    static final String CAR_INPUT_FORMAT = "[0-9]+,[0-9]+,[0-9]+,[0-9]+";

    static final String QUIT_COMMAND = "quit";

    static final String INVALID_COMMAND_ERROR = "Error: \"%s\" is not a valid command";
    static final String INVALID_FILE_LINE_ERROR = "Error: \"%s\" is not a valid line";
    static final String UNKNOWN_NODE_ERROR = "Error: 404 Node \"%s\" not found";

    static final int STREET_MAX_LENGTH = 10000;
    static final int STREET_MIN_LENGTH = 10;

    static final int STREET_MAX_SPEED_LIMIT = 40;
    static final int STREET_MIN_SPEED_LIMIT = 5;

    private Scanner scanner;

    public static void main(String[] args) {
        
        

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

    }

    void handleCommandWithParamenter(String[] input) {
        switch (input[0]) {
            case "load":
                handleLoad(input[1]);
                break;
            case "simulate":
                
                break;
            case "position":
                
                break;
        }
    }

    void handleLoad(String param) {

        SimulationFileLoader loader;
        try {
            loader = new SimulationFileLoader(param);

            List<String> streetConstructors = loader.loadStreets();
            List<String> crossingConstructors = loader.loadCrossings();
            List<String> carConstructors = loader.loadCars();

            SimulationGraph newGraph = new SimulationGraph();

            //crossings
            for (String string : crossingConstructors) {
                if (!string.matches(CROSSING_INPUT_FORMAT)) {
                    System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                    return;
                }
                
                String[] subStrings = string.split(":|t");

                int nodeId = Integer.parseInt(subStrings[0]);
                int greenPhaseDuration = Integer.parseInt(subStrings[1]);

                if (nodeId < 0 || greenPhaseDuration < 0) {
                    System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                    return;
                }

                newGraph.addNode(nodeId, greenPhaseDuration);

            }

            //streets.sim
            for (String string : streetConstructors) {
                if (!string.matches(STREET_INPUT_FORMAT)) {
                    System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                    return;
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
                    return;
                }

                //check if both nodes exist
                if (!newGraph.containsNode(startingNodeId)) {
                    System.err.println(String.format(UNKNOWN_NODE_ERROR, startingNodeId));
                    return;
                }
                if (!newGraph.containsNode(endNodeId)) {
                    System.err.println(String.format(UNKNOWN_NODE_ERROR, endNodeId));
                    return;
                }

                newGraph.addStreet(startingNodeId, endNodeId, streetLength, streetType, speedLimit);

            }

            //cars.sim
            for (String string : carConstructors) {
                if (!string.matches(CAR_INPUT_FORMAT)) {
                    System.err.println(String.format(INVALID_FILE_LINE_ERROR, string));
                    return;
                }

                //TODO continue here

            }


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }



    }

}
