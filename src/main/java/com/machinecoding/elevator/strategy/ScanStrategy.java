package com.machinecoding.elevator.strategy;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;
import com.machinecoding.elevator.model.ExternalRequest;

import java.util.Comparator;
import java.util.List;

public class ScanStrategy implements SchedulingStrategy {
    @Override
    public Elevator assignElevator(List<Elevator> elevators, ExternalRequest request) {
        return elevators.stream()
                .filter(elevator -> elevator.getStatus() != ElevatorStatus.MAINTENANCE)
                .min(Comparator
                        .comparingInt((Elevator elevator) -> scanScore(elevator, request))
                        .thenComparingInt(Elevator::pendingStopCount)
                        .thenComparingInt(Elevator::getId))
                .orElseThrow(() -> new IllegalStateException("No available elevators"));
    }

    private int scanScore(Elevator elevator, ExternalRequest request) {
        int distance = Math.abs(elevator.getCurrentFloor() - request.getFloor());

        if (elevator.getDirection() == Direction.IDLE) {
            return distance;
        }

        boolean sameDirection = elevator.getDirection() == request.getDirection();
        boolean onTheWay = elevator.getDirection() == Direction.UP
                ? request.getFloor() >= elevator.getCurrentFloor()
                : request.getFloor() <= elevator.getCurrentFloor();

        if (sameDirection && onTheWay) {
            return distance;
        }

        return distance + 1000;
    }
}

