package com.machinecoding.elevator.controller;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorSnapshot;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.observer.ElevatorObserver;
import com.machinecoding.elevator.service.AssignmentService;
import com.machinecoding.elevator.strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorController {
    private final List<Elevator> elevators;
    private final AssignmentService assignmentService;
    private final ReentrantLock assignmentLock = new ReentrantLock(true);

    public ElevatorController(List<Elevator> elevators, SchedulingStrategy schedulingStrategy) {
        if (elevators == null || elevators.isEmpty()) {
            throw new IllegalArgumentException("At least one elevator is required");
        }
        this.elevators = List.copyOf(elevators);
        this.assignmentService = new AssignmentService(Objects.requireNonNull(schedulingStrategy));
    }

    public int submitExternalRequest(ExternalRequest request) {
        Objects.requireNonNull(request);
        assignmentLock.lock();
        try {
            Elevator elevator = assignmentService.assign(elevators, request);
            elevator.addStop(request.getFloor());
            return elevator.getId();
        } finally {
            assignmentLock.unlock();
        }
    }

    public void submitInternalRequest(InternalRequest request) {
        Objects.requireNonNull(request);
        Elevator elevator = findElevator(request.getElevatorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown elevator id: " + request.getElevatorId()
                ));
        elevator.addStop(request.getFloor());
    }

    public void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        assignmentLock.lock();
        try {
            assignmentService.setSchedulingStrategy(schedulingStrategy);
        } finally {
            assignmentLock.unlock();
        }
    }

    public void addObserver(ElevatorObserver observer) {
        elevators.forEach(elevator -> elevator.addObserver(observer));
    }

    public void putInMaintenance(int elevatorId) {
        findElevator(elevatorId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown elevator id: " + elevatorId))
                .enterMaintenance();
    }

    public void releaseFromMaintenance(int elevatorId) {
        findElevator(elevatorId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown elevator id: " + elevatorId))
                .exitMaintenance();
    }

    public List<ElevatorSnapshot> snapshots() {
        List<ElevatorSnapshot> snapshots = new ArrayList<>();
        for (Elevator elevator : elevators) {
            snapshots.add(elevator.snapshot());
        }
        return snapshots;
    }

    public List<Elevator> elevators() {
        return elevators;
    }

    private Optional<Elevator> findElevator(int elevatorId) {
        return elevators.stream()
                .filter(elevator -> elevator.getId() == elevatorId)
                .findFirst();
    }
}

