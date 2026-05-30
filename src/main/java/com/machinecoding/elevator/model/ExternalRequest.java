package com.machinecoding.elevator.model;

public class ExternalRequest {
    private final int floor;
    private final Direction direction;

    public ExternalRequest(int floor, Direction direction) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        if (direction == null || direction == Direction.IDLE) {
            throw new IllegalArgumentException("External request direction must be UP or DOWN");
        }
        this.floor = floor;
        this.direction = direction;
    }

    public int getFloor() {
        return floor;
    }

    public Direction getDirection() {
        return direction;
    }
}
