package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;

public class MaintenanceState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        elevator.setStatus(ElevatorStatus.MAINTENANCE);
    }
}

