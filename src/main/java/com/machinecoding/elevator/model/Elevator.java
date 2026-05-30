package com.machinecoding.elevator.model;

import com.machinecoding.elevator.observer.ElevatorObserver;
import com.machinecoding.elevator.state.ElevatorState;
import com.machinecoding.elevator.state.IdleState;
import com.machinecoding.elevator.state.MaintenanceState;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Elevator {
    private static final int DEFAULT_MOVE_DELAY_MILLIS = 150;
    private static final int QUEUE_INITIAL_CAPACITY = 32;

    private final int id;
    private final int minFloor;
    private final int maxFloor;
    private final Door door;
    private final PriorityBlockingQueue<Integer> upQueue;
    private final PriorityBlockingQueue<Integer> downQueue;
    private final List<ElevatorObserver> observers;

    private volatile int currentFloor;
    private volatile Direction direction;
    private volatile ElevatorStatus status;
    private volatile ElevatorState state;

    public Elevator(int id, int minFloor, int maxFloor) {
        if (id <= 0) {
            throw new IllegalArgumentException("Elevator id must be positive");
        }
        if (minFloor < 0 || maxFloor < minFloor) {
            throw new IllegalArgumentException("Invalid floor range");
        }
        this.id = id;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.currentFloor = minFloor;
        this.direction = Direction.IDLE;
        this.status = ElevatorStatus.IDLE;
        this.state = new IdleState();
        this.door = new Door();
        this.upQueue = new PriorityBlockingQueue<>(QUEUE_INITIAL_CAPACITY);
        this.downQueue = new PriorityBlockingQueue<>(QUEUE_INITIAL_CAPACITY, Comparator.reverseOrder());
        this.observers = new CopyOnWriteArrayList<>();
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

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public ElevatorStatus getStatus() {
        return status;
    }

    public ElevatorState getState() {
        return state;
    }

    public Door getDoor() {
        return door;
    }

    public PriorityBlockingQueue<Integer> getUpQueue() {
        return upQueue;
    }

    public PriorityBlockingQueue<Integer> getDownQueue() {
        return downQueue;
    }

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    public synchronized void addStop(int floor) {
        validateFloor(floor);
        if (status == ElevatorStatus.MAINTENANCE) {
            throw new IllegalStateException("Elevator " + id + " is in maintenance");
        }

        if (floor >= currentFloor) {
            upQueue.offer(floor);
        } else {
            downQueue.offer(floor);
        }
        notifyObservers("stop queued: " + floor);
    }

    public Integer pollNextUpStop() {
        return upQueue.poll();
    }

    public Integer pollNextDownStop() {
        return downQueue.poll();
    }

    public boolean hasPendingStops() {
        return !upQueue.isEmpty() || !downQueue.isEmpty();
    }

    public boolean hasUpStops() {
        return !upQueue.isEmpty();
    }

    public boolean hasDownStops() {
        return !downQueue.isEmpty();
    }

    public int pendingStopCount() {
        return upQueue.size() + downQueue.size();
    }

    public synchronized void setDirection(Direction direction) {
        if (this.direction == direction) {
            return;
        }
        this.direction = direction;
        notifyObservers("direction changed");
    }

    public synchronized void setStatus(ElevatorStatus status) {
        if (this.status == status) {
            return;
        }
        this.status = status;
        notifyObservers("status changed");
    }

    public synchronized void setState(ElevatorState state) {
        this.state = state;
    }

    public synchronized void moveToFloor(int targetFloor) {
        validateFloor(targetFloor);
        if (targetFloor == currentFloor) {
            notifyObservers("already at floor " + targetFloor);
            return;
        }

        direction = Direction.between(currentFloor, targetFloor);
        status = ElevatorStatus.MOVING;
        notifyObservers("movement started toward " + targetFloor);

        while (currentFloor != targetFloor) {
            currentFloor += direction == Direction.UP ? 1 : -1;
            notifyObservers("floor changed");
            sleep(DEFAULT_MOVE_DELAY_MILLIS);
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
        upQueue.clear();
        downQueue.clear();
        direction = Direction.IDLE;
        status = ElevatorStatus.MAINTENANCE;
        state = new MaintenanceState();
        notifyObservers("entered maintenance");
    }

    public synchronized void exitMaintenance() {
        status = ElevatorStatus.IDLE;
        state = new IdleState();
        notifyObservers("exited maintenance");
    }

    public synchronized ElevatorSnapshot snapshot() {
        return new ElevatorSnapshot(
                id,
                currentFloor,
                direction,
                status,
                pendingStopCount(),
                minFloor,
                maxFloor
        );
    }

    private void validateFloor(int floor) {
        if (floor < minFloor || floor > maxFloor) {
            throw new IllegalArgumentException(
                    "Floor " + floor + " is outside " + minFloor + "-" + maxFloor
            );
        }
    }

    private void notifyObservers(String event) {
        for (ElevatorObserver observer : observers) {
            observer.update(id, currentFloor, direction, status, event);
        }
    }

    private void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
