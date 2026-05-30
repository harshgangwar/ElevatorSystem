package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Elevator;

public interface ElevatorState {
    void handle(Elevator elevator);
}

