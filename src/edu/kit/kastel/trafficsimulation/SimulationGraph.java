package edu.kit.kastel.trafficsimulation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import edu.kit.kastel.trafficsimulation.StreetNodes.Crossing;
import edu.kit.kastel.trafficsimulation.StreetNodes.Roundabout;
import edu.kit.kastel.trafficsimulation.StreetNodes.StreetNode;

public class SimulationGraph {

    private int streetIdentifierCounter = 0;
    
    private HashMap<Integer, Street> streetMap = new HashMap<>();

    private HashMap<Integer, StreetNode> nodeMap = new HashMap<>();

    private HashMap<Integer, Car> carMap = new HashMap<>();

    public Car getCarById(int id) {
        return carMap.get(id);
    }

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
            nodeMap.put(id, new Roundabout(this, id));
        } else if (greenPhaseDuration > 0) {
            nodeMap.put(id, new Crossing(this, id, greenPhaseDuration));
        } else {
            throw new IllegalArgumentException("Error: The greenPhaseDuration of a node cannot be less than 0");
        }

    }

    public void addStreet(int startNode, int endNode, int length, int type, int maxSpeed) {

        int id = getNewStreetID();

        Street street = new Street(id, this, startNode, endNode, length, type, maxSpeed);

        streetMap.put(id, street);

        nodeMap.get(startNode).addOutputStreet(id);
        nodeMap.get(endNode).addInputStreet(id);

    }

    public Street getStreetById(int id) {
        return streetMap.get(id);
    }

    public boolean isValid() {
        //check if all streets are valid
        for (Entry<Integer, Street> streetEntry : streetMap.entrySet()) {
            if (!streetEntry.getValue().isValid()) {
                return false;
            }
        }

        //check if all nodes are valid
        for (Entry<Integer, StreetNode> nodeEntry : nodeMap.entrySet()) {
            if (!nodeEntry.getValue().isValid()) {
                return false;
            }
        }

        //if no invalid street or node was found the graph is valid
        return true;
    }

    public boolean containsCar(int carID) {
        for (Entry<Integer, Street> streetEntry : streetMap.entrySet()) {
            if (streetEntry.getValue().containsCar(carID)) {
                return true;
            }
        }
        return false;
    }

    public boolean streetIdHasSpaceForCar(int streetID) {
        return streetMap.get(streetID).hasSpaceForCar();
    }

    public void addCarToStreet(int streetID, Car car) {
        carMap.put(car.getId(), car);
        streetMap.get(streetID).addCar(car.getId());
    }
    
    public int getCarPosition(int carId) {
        Car car = carMap.get(carId);
        return car.getPositionOnStreet();
    }

    public int getCarOnStreetId(int carId) {
        Car car = carMap.get(carId);
        return car.getOnStreetId();
    }

    public int getCarSpeed(int carId) {
        Car car = carMap.get(carId);
        return car.getSpeed();
    }

    public int getCarPositionOnStreet(int carId) {
        Car car = carMap.get(carId);
        return car.getPositionOnStreet();
    }

    public void tick() {
        //set all cars to not crossed this tick
        //and update their speed
        for (Entry<Integer, Car> carEntry : carMap.entrySet()) {
            Car car = carEntry.getValue();
            car.setAlreadyCrossedThisTick(false);
            int maxSpeed = streetMap.get(car.getOnStreetId()).getMaxSpeed();
            car.updateSpeed(maxSpeed);
        }

        //update all car positions
        for (Entry<Integer, Street> streetEntry : streetMap.entrySet()) {
            streetEntry.getValue().updateCarPositions();
        }

        //handle crossing
        for (Entry<Integer, StreetNode> nodeEntry : nodeMap.entrySet()) {
            nodeEntry.getValue().handleCrossing();
        }

        

        
    }

}
