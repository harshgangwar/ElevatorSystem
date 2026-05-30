package com.machinecoding.elevator.observer;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.ElevatorStatus;

public interface ElevatorObserver {
    void update(int elevatorId, int floor, Direction direction, ElevatorStatus status, String event);
}

