package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;

public class MovingDownState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        Integer nextFloor = elevator.pollNextDownStop();

        if (nextFloor == null) {
            if (elevator.hasUpStops()) {
                elevator.setDirection(Direction.UP);
                elevator.setState(new MovingUpState());
            } else {
                elevator.setDirection(Direction.IDLE);
                elevator.setState(new IdleState());
            }
            return;
        }

        if (nextFloor > elevator.getCurrentFloor()) {
            elevator.getUpQueue().offer(nextFloor);
            elevator.setState(new MovingUpState());
            return;
        }

        elevator.moveToFloor(nextFloor);
        elevator.setState(new DoorOpenState());
    }
}

