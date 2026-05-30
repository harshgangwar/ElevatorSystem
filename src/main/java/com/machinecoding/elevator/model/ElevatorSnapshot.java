package com.machinecoding.elevator.model;

public record ElevatorSnapshot(
        int id,
        int currentFloor,
        Direction direction,
        ElevatorStatus status,
        int pendingStops,
        int minFloor,
        int maxFloor
) {
    public boolean isAvailable() {
        return status != ElevatorStatus.MAINTENANCE;
    }
}

