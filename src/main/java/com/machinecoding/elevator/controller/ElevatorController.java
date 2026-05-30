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

public class ElevatorController {
    private final List<Elevator> elevators;
    private final AssignmentService assignmentService;

    public ElevatorController(List<Elevator> elevators, SchedulingStrategy schedulingStrategy) {
        if (elevators == null || elevators.isEmpty()) {
            throw new IllegalArgumentException("At least one elevator is required");
        }
        this.elevators = List.copyOf(elevators);
        this.assignmentService = new AssignmentService(Objects.requireNonNull(schedulingStrategy));
    }

    // Assigning a request to an elevator
    public synchronized int submitExternalRequest(ExternalRequest request) {
        Objects.requireNonNull(request);
        Elevator elevator = assignmentService.assign(elevators, request);
        elevator.addStop(request.getFloor());
        return elevator.getId();
    }

    /**
     * Letting each elevator move independently.
     * Each elevator has its own worker thread in ElevatorWorker.java. That means elevator 1 can move while elevator 2 is also moving. We do not want a big synchronized controller method to manage actual movement, because then one slow elevator could block unrelated elevators.
     * Inside Elevator.java, each elevator protects its own mutable state with its own lock:
         * private final ReentrantLock lock;
         * private volatile int currentFloor;
         * private volatile Direction direction;
         * private volatile ElevatorStatus status;
     * @param request
     */
    public void submitInternalRequest(InternalRequest request) {
        Objects.requireNonNull(request);
        Elevator elevator = findElevator(request.getElevatorId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown elevator id: " + request.getElevatorId()
                ));
        elevator.addStop(request.getFloor());
    }

    public synchronized void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        assignmentService.setSchedulingStrategy(schedulingStrategy);
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
