package com.machinecoding.elevator.state;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ElevatorStatus;

public class IdleState implements ElevatorState {
    @Override
    public void handle(Elevator elevator) {
        elevator.setStatus(ElevatorStatus.IDLE);

        if (elevator.hasUpStops()) {
            elevator.setDirection(Direction.UP);
            elevator.setState(new MovingUpState());
            return;
        }

        if (elevator.hasDownStops()) {
            elevator.setDirection(Direction.DOWN);
            elevator.setState(new MovingDownState());
            return;
        }

        elevator.setDirection(Direction.IDLE);
    }
}

