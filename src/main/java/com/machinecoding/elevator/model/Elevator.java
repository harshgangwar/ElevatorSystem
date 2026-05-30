package com.machinecoding.elevator.model;

import com.machinecoding.elevator.observer.ElevatorObserver;
import com.machinecoding.elevator.state.ElevatorState;
import com.machinecoding.elevator.state.IdleState;
import com.machinecoding.elevator.state.MaintenanceState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Elevator {
    private final int id;
    private final int minFloor;
    private final int maxFloor;
    private final Door door;
    private final Queue<Integer> upStops;
    private final Queue<Integer> downStops;
    private final List<ElevatorObserver> observers;

    private int currentFloor;
    private Direction direction;
    private ElevatorStatus status;
    private ElevatorState state;

    public Elevator(int id, int minFloor, int maxFloor) {
        this.id = id;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = minFloor;
        this.direction = Direction.IDLE;
        this.status = ElevatorStatus.IDLE;
        this.state = new IdleState();
        this.door = new Door();
        this.upStops = new PriorityQueue<>();
        this.downStops = new PriorityQueue<>(Collections.reverseOrder());
        this.observers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized Direction getDirection() {
        return direction;
    }

    public synchronized ElevatorStatus getStatus() {
        return status;
    }

    public synchronized int pendingStopCount() {
        return upStops.size() + downStops.size();
    }

    public synchronized void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public synchronized void addStop(int floor) {
        validateFloor(floor);
        if (status == ElevatorStatus.MAINTENANCE) {
            throw new IllegalStateException("Elevator is in maintenance");
        }

        if (floor >= currentFloor) {
            upStops.offer(floor);
        } else {
            downStops.offer(floor);
        }
        notifyObservers("stop added: " + floor);
    }

    public synchronized void processNextStep() {
        state.handle(this);
    }

    public synchronized boolean isIdle() {
        return status == ElevatorStatus.IDLE
                && direction == Direction.IDLE
                && !hasPendingStops()
                && state instanceof IdleState;
    }

    public synchronized boolean hasPendingStops() {
        return !upStops.isEmpty() || !downStops.isEmpty();
    }

    public synchronized boolean hasUpStops() {
        return !upStops.isEmpty();
    }

    public synchronized boolean hasDownStops() {
        return !downStops.isEmpty();
    }

    public synchronized Integer pollNextUpStop() {
        return upStops.poll();
    }

    public synchronized Integer pollNextDownStop() {
        return downStops.poll();
    }

    public synchronized void setDirection(Direction direction) {
        this.direction = direction;
    }

    public synchronized void setStatus(ElevatorStatus status) {
        this.status = status;
    }

    public synchronized void setState(ElevatorState state) {
        this.state = state;
    }

    public synchronized void moveToFloor(int targetFloor) {
        validateFloor(targetFloor);
        direction = Direction.between(currentFloor, targetFloor);
        status = ElevatorStatus.MOVING;

        while (currentFloor != targetFloor) {
            currentFloor += direction == Direction.UP ? 1 : -1;
            notifyObservers("reached floor " + currentFloor);
        }
    }

    public synchronized void openDoor() {
        status = ElevatorStatus.DOOR_OPEN;
        direction = Direction.IDLE;
        door.open();
        notifyObservers("door opened");
    }

    public synchronized void closeDoor() {
        door.close();
        status = ElevatorStatus.IDLE;
        notifyObservers("door closed");
    }

    public synchronized void enterMaintenance() {
        upStops.clear();
        downStops.clear();
        direction = Direction.IDLE;
        status = ElevatorStatus.MAINTENANCE;
        state = new MaintenanceState();
    }

    public synchronized void exitMaintenance() {
        status = ElevatorStatus.IDLE;
        state = new IdleState();
    }

    @Override
    public synchronized String toString() {
        return "Elevator{id=" + id
                + ", floor=" + currentFloor
                + ", direction=" + direction
                + ", status=" + status
                + ", pendingStops=" + pendingStopCount()
                + '}';
    }

    private void validateFloor(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException("Invalid floor: " + floor);
        }
    }

    private void notifyObservers(String event) {
        for (ElevatorObserver observer : observers) {
            observer.update(id, currentFloor, direction, status, event);
        }
    }
}
