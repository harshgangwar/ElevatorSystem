package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;

public class MovingUpState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        Integer nextFloor = elevator.pollNextUpStop();

        if (nextFloor == null) {
            if (elevator.hasDownStops()) {
                elevator.setDirection(Direction.DOWN);
                elevator.setState(new MovingDownState());
            } else {
                elevator.setDirection(Direction.IDLE);
                elevator.setState(new IdleState());
            }
            return;
        }

        if (nextFloor < elevator.getCurrentFloor()) {
            elevator.getDownQueue().offer(nextFloor);
            elevator.setState(new MovingDownState());
            return;
        }

        elevator.moveToFloor(nextFloor);
        elevator.setState(new DoorOpenState());
    }
}

