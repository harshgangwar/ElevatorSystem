package com.machinecoding.elevator.service;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.strategy.SchedulingStrategy;

import java.util.List;
import java.util.Objects;

public class AssignmentService {
    private volatile SchedulingStrategy schedulingStrategy;

    public AssignmentService(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = Objects.requireNonNull(schedulingStrategy);
    }

    public Elevator assign(List<Elevator> elevators, ExternalRequest request) {
        return schedulingStrategy.assignElevator(elevators, request);
    }

    public void setSchedulingStrategy(SchedulingStrategy schedulingStrategy) {
        this.schedulingStrategy = Objects.requireNonNull(schedulingStrategy);
    }
}

