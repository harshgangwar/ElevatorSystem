package com.machinecoding.elevator.observer;

import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.ElevatorStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonitoringSystem implements ElevatorObserver {
    private final List<String> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void update(int elevatorId, int floor, Direction direction, ElevatorStatus status, String event) {
        events.add(Instant.now()
                + " elevator=" + elevatorId
                + " floor=" + floor
                + " direction=" + direction
                + " status=" + status
                + " event=" + event);
    }

    public List<String> snapshot() {
        synchronized (events) {
            return List.copyOf(events);
        }
    }
}

