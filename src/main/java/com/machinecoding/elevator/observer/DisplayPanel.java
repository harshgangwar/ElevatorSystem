package com.machinecoding.elevator.observer;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.ElevatorStatus;

public class DisplayPanel implements ElevatorObserver {
    @Override
    public void update(int elevatorId, int floor, Direction direction, ElevatorStatus status, String event) {
        System.out.printf(
                "Display | elevator=%d floor=%d direction=%s status=%s event=%s%n",
                elevatorId,
                floor,
                direction,
                status,
                event
        );
    }
}

