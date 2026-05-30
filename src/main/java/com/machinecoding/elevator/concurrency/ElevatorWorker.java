package com.machinecoding.elevator.concurrency;

import com.machinecoding.elevator.model.Elevator;

import java.util.concurrent.TimeUnit;

public class ElevatorWorker implements Runnable {
    private static final int LOOP_DELAY_MILLIS = 100;

    private final Elevator elevator;

    public ElevatorWorker(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            elevator.getState().handle(elevator);
            try {
                TimeUnit.MILLISECONDS.sleep(LOOP_DELAY_MILLIS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

