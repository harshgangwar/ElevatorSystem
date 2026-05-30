package com.machinecoding.elevator.strategy;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;

import java.util.List;

public interface SchedulingStrategy {
    Elevator assignElevator(List<Elevator> elevators, ExternalRequest request);
}

