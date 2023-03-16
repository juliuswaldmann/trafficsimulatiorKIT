package edu.kit.kastel.trafficsimulation;

import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.kit.kastel.trafficsimulation.StreetNodes.Crossing;
import edu.kit.kastel.trafficsimulation.StreetNodes.Roundabout;
import edu.kit.kastel.trafficsimulation.StreetNodes.StreetNode;

public class SimulationGraph {

    private int streetIdentifierCounter = 0;
    
    private HashMap<Integer, Street> streetMap = new HashMap<>();

    private HashMap<Integer, StreetNode> nodeMap = new HashMap<>();

    /** 
     * This is an extended form on an adjacencyList commonly used when using graph theory in computer science.
     * For each Node (represented by an Integer) it stores a set of all connected nodes (from this node to the other)
     * and maps the according street from the streetMap to this connection.
     * We do not just store the Streets in this map because having a seperate street map makes it way easier to create
     * ids for each street. The id of a street is just its key in street map.
     */
    private LinkedHashMap<Integer, HashMap<Integer, Integer>> adjacencyMap = new LinkedHashMap<>();

    private int getNewStreetID() {
        return streetIdentifierCounter++;
    }

    public boolean containsNode(int id) {
        return nodeMap.containsKey(id);
    }

    public boolean containsStreet(int id) {
        return streetMap.containsKey(id);
    }

    public void addNode(int id, int greenPhaseDuration) {

        if (greenPhaseDuration == 0) {
            nodeMap.put(id, new Roundabout(id));
        } else if (greenPhaseDuration > 0) {
            nodeMap.put(id, new Crossing(id, greenPhaseDuration));
        } else {
            throw new IllegalArgumentException("Error: The greenPhaseDuration of a node cannot be less than 0");
        }

    }

    public void addStreet(int startNode, int endNode, int length, int type, int maxSpeed) {

        int id = getNewStreetID();

        Street street = new Street(startNode, endNode, length, type, maxSpeed);

        streetMap.put(id, street);

        nodeMap.get(startNode).addOutputStreet(id);
        nodeMap.get(endNode).addInputStreet(id);

    }

    

}
