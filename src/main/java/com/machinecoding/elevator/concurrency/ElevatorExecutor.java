package com.machinecoding.elevator.concurrency;

import com.machinecoding.elevator.model.Elevator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ElevatorExecutor implements AutoCloseable {
    private final ExecutorService executorService;

    public ElevatorExecutor(int elevatorCount) {
        this.executorService = Executors.newFixedThreadPool(elevatorCount);
    }

    public void start(List<Elevator> elevators) {
        for (Elevator elevator : elevators) {
            executorService.submit(new ElevatorWorker(elevator));
        }
    }

    public List<Runnable> shutdownNow() {
        return new ArrayList<>(executorService.shutdownNow());
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}

