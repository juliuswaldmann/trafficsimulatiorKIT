package edu.kit.kastel.trafficsimulation.StreetNodes;

import java.util.ArrayList;

public abstract class StreetNode {
    protected String typeName;

    protected int nodeID;

    protected ArrayList<Integer> connectedInputStreets = new ArrayList<>();
    protected ArrayList<Integer> connectedOutputStreets = new ArrayList<>();

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
}
