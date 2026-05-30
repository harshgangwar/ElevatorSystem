package com.machinecoding.elevator.strategy;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;
import com.machinecoding.elevator.model.ExternalRequest;

import java.util.Comparator;
import java.util.List;

public class ZoningStrategy implements SchedulingStrategy {
    @Override
    public Elevator assignElevator(List<Elevator> elevators, ExternalRequest request) {
        int availableCount = (int) elevators.stream()
                .filter(elevator -> elevator.getStatus() != ElevatorStatus.MAINTENANCE)
                .count();

        if (availableCount == 0) {
            throw new IllegalStateException("No available elevators");
        }

        int highestFloor = elevators.stream()
                .mapToInt(Elevator::getMaxFloor)
                .max()
                .orElse(request.getFloor());
        int zoneSize = Math.max(1, (highestFloor + 1) / availableCount);
        int preferredZone = request.getFloor() / zoneSize;

        return elevators.stream()
                .filter(elevator -> elevator.getStatus() != ElevatorStatus.MAINTENANCE)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> Math.abs(zoneOf(elevator, zoneSize) - preferredZone))
                        .thenComparingInt(elevator -> Math.abs(elevator.getCurrentFloor() - request.getFloor()))
                        .thenComparingInt(Elevator::pendingStopCount)
                        .thenComparingInt(Elevator::getId))
                .orElseThrow(() -> new IllegalStateException("No available elevators"));
    }

    private int zoneOf(Elevator elevator, int zoneSize) {
        return (elevator.getId() - 1);
    }
}
