package com.machinecoding.elevator.strategy;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;
import com.machinecoding.elevator.model.ExternalRequest;

import java.util.Comparator;
import java.util.List;

public class ZoningStrategy implements SchedulingStrategy {
    @Override
    public Elevator assignElevator(List<Elevator> elevators, ExternalRequest request) {
        int maxFloor = elevators.stream()
                .mapToInt(Elevator::getMaxFloor)
                .max()
                .orElse(request.getFloor());
        int zoneSize = Math.max(1, (maxFloor + 1) / elevators.size());

        return elevators.stream()
                .filter(elevator -> elevator.getStatus() != ElevatorStatus.MAINTENANCE)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> zoneDistance(elevator, request, zoneSize))
                        .thenComparingInt(elevator -> Math.abs(elevator.getCurrentFloor() - request.getFloor())))
                .orElseThrow(() -> new IllegalStateException("No available elevators"));
    }

    private int zoneDistance(Elevator elevator, ExternalRequest request, int zoneSize) {
        int elevatorZone = (elevator.getId() - 1);
        int requestZone = request.getFloor() / zoneSize;
        return Math.abs(elevatorZone - requestZone);
    }
}
