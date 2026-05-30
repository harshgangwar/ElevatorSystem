package com.machinecoding.elevator.model;

public class ExternalRequest extends ElevatorRequest {
    private final Direction direction;

    public ExternalRequest(int floor, Direction direction) {
        super(floor);
        if (direction == null || direction == Direction.IDLE) {
            throw new IllegalArgumentException("External request must have UP or DOWN direction");
        }
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "ExternalRequest{requestId=" + getRequestId()
                + ", floor=" + getFloor()
                + ", direction=" + direction
                + '}';
    }
}

