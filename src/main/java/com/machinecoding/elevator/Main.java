package com.machinecoding.elevator;

import com.machinecoding.elevator.controller.ElevatorController;
import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.observer.DisplayPanel;
import com.machinecoding.elevator.strategy.NearestCarStrategy;
import com.machinecoding.elevator.strategy.ScanStrategy;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ElevatorController.resetInstance();

        List<Elevator> elevators = List.of(
                new Elevator(1, 0, 20),
                new Elevator(2, 0, 20),
                new Elevator(3, 0, 20)
        );

        ElevatorController controller = ElevatorController.getInstance(
                elevators,
                new NearestCarStrategy()
        );
        controller.addObserver(new DisplayPanel());

        int elevatorId = controller.submitExternalRequest(new ExternalRequest(10, Direction.UP));
        controller.submitInternalRequest(new InternalRequest(elevatorId, 15));
        controller.submitExternalRequest(new ExternalRequest(3, Direction.UP));

        runUntilDone(controller);

        controller.setSchedulingStrategy(new ScanStrategy());
        controller.submitExternalRequest(new ExternalRequest(6, Direction.DOWN));
        runUntilDone(controller);

        System.out.println("Final state:");
        controller.getElevators().forEach(System.out::println);
    }

    private static void runUntilDone(ElevatorController controller) {
        while (controller.hasPendingRequests()) {
            controller.step();
        }
    }
}
