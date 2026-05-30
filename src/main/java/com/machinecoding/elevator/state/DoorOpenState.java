package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Elevator;

public class DoorOpenState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        elevator.openDoor();
        elevator.closeDoor();
        elevator.setState(new IdleState());
    }
}
