package com.machinecoding.elevator.model;

public class InternalRequest {
    private final int elevatorId;
    private final int destinationFloor;

    public InternalRequest(int elevatorId, int destinationFloor) {
        if (destinationFloor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        this.elevatorId = elevatorId;
        this.destinationFloor = destinationFloor;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }
}
