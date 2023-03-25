package edu.kit.kastel.trafficsimulation;

public class Car {

    private int currentSpeed;
    
    private int acceleration;

    private int wantedSpeed;

    /** The direction the car wants to go to. Starts with 0. */
    private int wantedDirection = 0;

    private int metersLeftToDrive;

    private boolean alreadyCrossedThisTick;

    private int positionOnStreet;

    private int onStreetID;

    private int id;

    public Car(int id,int acceleration, int wantedSpeed) {
        this.id = id;
        this.acceleration = acceleration;
        this.wantedSpeed = wantedSpeed;
    }

    public void setPositionOnStreet(int positionOnStreet) {
        this.positionOnStreet = positionOnStreet;
    }

    public int getPositionOnStreet() {
        return positionOnStreet;
    }

    public void setOnStreetId(int onStreetID) {
        this.onStreetID = onStreetID;
    }

    public int getOnStreetId() {
        return onStreetID;
    }

    public int getId() {
        return id;
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public void updateSpeed(int speedLimit) { //y
        //Increase speed by acceleration
        currentSpeed = Math.min(currentSpeed + acceleration, Math.min(wantedSpeed, speedLimit));
        metersLeftToDrive = currentSpeed;
    }

    public int getCurrentSpeed() { 
        return currentSpeed;
    }

    public void droveMeters(int meters) {
        metersLeftToDrive -= meters;
    }

    public int getMetersLeftToDrive() {
        return metersLeftToDrive;
    }

    public int getAndIncreaseWantedDirection() {
        int direction = wantedDirection;
        wantedDirection++;
        if (wantedDirection == 4) {
            wantedDirection = 0;
        }
        return direction;
    }

    public void setAlreadyCrossedThisTick(boolean b) {
        alreadyCrossedThisTick = b;
    }

    public boolean hasAlreadyCrossedThisTick() {
        return alreadyCrossedThisTick;
    }

}
