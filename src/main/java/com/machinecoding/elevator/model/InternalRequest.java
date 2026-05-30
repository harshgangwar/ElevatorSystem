package com.machinecoding.elevator.model;

public class InternalRequest extends ElevatorRequest {
    private final int elevatorId;

    public InternalRequest(int elevatorId, int destinationFloor) {
        super(destinationFloor);
        this.elevatorId = elevatorId;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    @Override
    public String toString() {
        return "InternalRequest{requestId=" + getRequestId()
                + ", elevatorId=" + elevatorId
                + ", destinationFloor=" + getFloor()
                + '}';
    }
}

