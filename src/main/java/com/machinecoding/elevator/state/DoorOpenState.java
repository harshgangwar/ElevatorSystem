package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Elevator;

import java.util.concurrent.TimeUnit;

public class DoorOpenState implements ElevatorState {
    private static final int DOOR_OPEN_MILLIS = 500;

    @Override
    public void handle(Elevator elevator) {
        elevator.openDoor();
        try {
            TimeUnit.MILLISECONDS.sleep(DOOR_OPEN_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        elevator.closeDoor();
        elevator.setState(new IdleState());
    }
}

