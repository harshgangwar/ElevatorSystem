package com.machinecoding.elevator.model;

public enum Direction {
    UP,
    DOWN,
    IDLE;

    public static Direction between(int fromFloor, int toFloor) {
        if (toFloor > fromFloor) {
            return UP;
        }
        if (toFloor < fromFloor) {
            return DOWN;
        }
        return IDLE;
    }
}

