package edu.kit.kastel.trafficsimulation;

public class Car {
    private int currentStreet;

    private int currentSpeed;
    
    private int acceleration;

    private int wantedSpeed;

    private int wantedDirection;

    public void updateSpeed(int speedLimit) {
        //Increase speed by acceleration
        currentSpeed = Math.min(currentSpeed + acceleration, Math.min(wantedSpeed, speedLimit));
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

}
