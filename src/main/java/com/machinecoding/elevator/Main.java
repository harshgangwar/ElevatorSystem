package com.machinecoding.elevator;

import com.machinecoding.elevator.concurrency.ElevatorExecutor;
import com.machinecoding.elevator.controller.ElevatorController;
import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.observer.DisplayPanel;
import com.machinecoding.elevator.observer.MonitoringSystem;
import com.machinecoding.elevator.service.RequestProcessor;
import com.machinecoding.elevator.strategy.NearestCarStrategy;
import com.machinecoding.elevator.strategy.ScanStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Elevator> elevators = List.of(
                new Elevator(1, 0, 20),
                new Elevator(2, 0, 20),
                new Elevator(3, 0, 20)
        );

        ElevatorController controller = new ElevatorController(elevators, new NearestCarStrategy());
        controller.addObserver(new DisplayPanel());
        MonitoringSystem monitoringSystem = new MonitoringSystem();
        controller.addObserver(monitoringSystem);

        RequestProcessor requestProcessor = new RequestProcessor(controller);

        try (ElevatorExecutor executor = new ElevatorExecutor(elevators.size())) {
            executor.start(elevators);

            int firstElevator = requestProcessor.process(new ExternalRequest(10, Direction.UP));
            requestProcessor.process(new InternalRequest(firstElevator, 16));
            requestProcessor.process(new ExternalRequest(3, Direction.UP));
            requestProcessor.process(new ExternalRequest(15, Direction.DOWN));

            TimeUnit.SECONDS.sleep(3);

            controller.setSchedulingStrategy(new ScanStrategy());
            requestProcessor.process(new ExternalRequest(6, Direction.DOWN));
            requestProcessor.process(new InternalRequest(2, 1));

            TimeUnit.SECONDS.sleep(6);

            System.out.println();
            System.out.println("Final snapshots:");
            controller.snapshots().forEach(System.out::println);
            System.out.println("Monitoring events captured: " + monitoringSystem.snapshot().size());
        }
    }
}

