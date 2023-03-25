package edu.kit.kastel.trafficsimulation;

/**
 * this class represents a car in the simulation
 * 
 * @author uxler
 * @version 1.0
 */
public class Car {

    /** Current speed of the car */
    private int currentSpeed;
    
    /** Acceleration of the car. */
    private int acceleration;

    /** Maximum speed */
    private int wantedSpeed;

    /** The direction the car wants to go to. Starts with 0. */
    private int wantedDirection = 0;

    /** The meters the car has left in this tick */
    private int metersLeftToDriveThisTick;

    /** True if the car has already crossed a node in this tick or overtook a car */
    private boolean alreadyCrossedThisTick;

    /** Position of the car relative to beginning of the street */
    private int positionOnStreet;

    /** ID of the street the car is on */
    private int onStreetID;

    /** ID of the car */
    private int id;

    /** Constructor for a car
     * @param id ID of the car
     * @param acceleration Acceleration of the car
     * @param wantedSpeed Maximum speed of the car
     */
    public Car(int id, int acceleration, int wantedSpeed) {
        this.id = id;
        this.acceleration = acceleration;
        this.wantedSpeed = wantedSpeed;
    }

    /**
     * Sets the position of the car on the street
     * @param positionOnStreet Position of the car on the street
     */
    public void setPositionOnStreet(int positionOnStreet) {
        this.positionOnStreet = positionOnStreet;
    }

    /**
     * Returns the position of the car on the street
     * @return Position of the car on the street
     */
    public int getPositionOnStreet() {
        return positionOnStreet;
    }

    /**
     * Change the street the car is on by setting the ID of the street
     * @param onStreetID ID of the street the car is now on
     */
    public void setOnStreetId(int onStreetID) {
        this.onStreetID = onStreetID;
    }

    /**
     * Returns the ID of the street the car is on
     * @return ID of the street the car is on
     */
    public int getOnStreetId() {
        return onStreetID;
    }

    /**
     * Returns the ID of the car
     * @return ID of the car
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the speed of the car
     * @return Speed of the car
     */
    public int getSpeed() {
        return currentSpeed;
    }

    /**
     * sets the speed of the car
     * @param speed the speed the car should have 
     */
    public void setSpeed(int speed) {
        currentSpeed = speed;
    }

    /**
     * updates the speed of the car by adding the acceleration to the current speed
     * and limiting it to the maximum speed
     * @param speedLimit Maximum speed on the street the car is on
     */
    public void updateSpeed(int speedLimit) { //y
        //Increase speed by acceleration
        currentSpeed = Math.min(currentSpeed + acceleration, Math.min(wantedSpeed, speedLimit));
        metersLeftToDriveThisTick = currentSpeed;
    }

    /**
     * Returns the current speed of the car
     * @return Current speed of the car
     */
    public int getCurrentSpeed() { 
        return currentSpeed;
    }

    /**
     * Update the distance the car has left to drive in this tick
     * @param meters Meters the car has driven
     */
    public void droveMeters(int meters) {
        metersLeftToDriveThisTick -= meters;
    }

    /**
     * Returns the meters the car has left to drive in this tick
     * @return Meters the car has left to drive in this tick
     */
    public int getMetersLeftToDrive() {
        return metersLeftToDriveThisTick;
    }

    /**
     * Increments the direction by one and returns the new direction
     * @return New direction
     */
    public int getAndIncreaseWantedDirection() {
        int direction = wantedDirection;
        wantedDirection++;

        //Reset direction to 0 if it is bigger than 3
        wantedDirection %= 4;
        return direction;
    }

    /**
     * Sets if the car has already crossed a node or passed a car in this tick
     * @param b True if the car has already crossed a node or passed a car in this tick
     */
    public void setAlreadyCrossedThisTick(boolean b) {
        alreadyCrossedThisTick = b;
    }

    /**
     * Returns if the car has already crossed a node or passed a car in this tick
     * @return True if the car has already crossed a node or passed a car in this tick
     */
    public boolean hasAlreadyCrossedThisTick() {
        return alreadyCrossedThisTick;
    }

}
