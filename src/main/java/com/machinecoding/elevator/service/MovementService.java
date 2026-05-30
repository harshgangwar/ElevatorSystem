package com.machinecoding.elevator.service;

import com.machinecoding.elevator.model.Elevator;

public class MovementService {
    public void move(Elevator elevator, int floor) {
        elevator.moveToFloor(floor);
    }
}

