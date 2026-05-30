package com.machinecoding.elevator.strategy;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;
import com.machinecoding.elevator.model.ExternalRequest;

import java.util.Comparator;
import java.util.List;

public class NearestCarStrategy implements SchedulingStrategy {
    @Override
    public Elevator assignElevator(List<Elevator> elevators, ExternalRequest request) {
        return elevators.stream()
                .filter(elevator -> elevator.getStatus() != ElevatorStatus.MAINTENANCE)
                .min(Comparator
                        .comparingInt((Elevator elevator) ->
                                Math.abs(elevator.getCurrentFloor() - request.getFloor()))
                        .thenComparingInt(Elevator::pendingStopCount)
                        .thenComparingInt(Elevator::getId))
                .orElseThrow(() -> new IllegalStateException("No available elevators"));
    }
}
