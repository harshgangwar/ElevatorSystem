package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;

public class MovingUpState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        Integer nextFloor = elevator.pollNextUpStop();

        if (nextFloor == null) {
            elevator.setDirection(Direction.IDLE);
            elevator.setState(new IdleState());
            return;
        }

        elevator.moveToFloor(nextFloor);
        elevator.setState(new DoorOpenState());
    }
}
