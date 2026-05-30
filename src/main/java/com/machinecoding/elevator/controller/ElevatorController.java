package com.machinecoding.elevator.controller;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.observer.ElevatorObserver;
import com.machinecoding.elevator.strategy.SchedulingStrategy;

import java.util.List;
import java.util.Objects;

public class ElevatorController {
    private static ElevatorController instance;

    private final List<Elevator> elevators;
    private SchedulingStrategy schedulingStrategy;

    private ElevatorController(List<Elevator> elevators, SchedulingStrategy schedulingStrategy) {
        this.elevators = List.copyOf(elevators);
        this.schedulingStrategy = Objects.requireNonNull(schedulingStrategy);
    }

    public static synchronized ElevatorController getInstance(
            List<Elevator> elevators,
            SchedulingStrategy schedulingStrategy
    ) {
        if (instance == null) {
            instance = new ElevatorController(elevators, schedulingStrategy);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    // Assigning a request to an elevator
    public synchronized int submitExternalRequest(ExternalRequest request) {
        Elevator elevator = schedulingStrategy.assignElevator(elevators, request);
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
    public synchronized void submitInternalRequest(InternalRequest request) {
        Elevator elevator = findElevator(request.getElevatorId());
        elevator.addStop(request.getDestinationFloor());
    }

    public synchronized void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = Objects.requireNonNull(schedulingStrategy);
    }

    public void step() {
        for (Elevator elevator : elevators) {
            elevator.processNextStep();
        }
    }

    public boolean hasPendingRequests() {
        return elevators.stream().anyMatch(elevator -> !elevator.isIdle());
    }

    public void addObserver(ElevatorObserver observer) {
        for (Elevator elevator : elevators) {
            elevator.addObserver(observer);
        }
    }

    public List<Elevator> getElevators() {
        return elevators;
    }

    private Elevator findElevator(int elevatorId) {
        return elevators.stream()
                .filter(elevator -> elevator.getId() == elevatorId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid elevator id: " + elevatorId));
    }
}
