package com.machinecoding.elevator.model;

public class Door {
    private DoorStatus status = DoorStatus.CLOSED;

    public DoorStatus getStatus() {
        return status;
    }

    public void open() {
        status = DoorStatus.OPEN;
    }

    public void close() {
        status = DoorStatus.CLOSED;
    }
}
