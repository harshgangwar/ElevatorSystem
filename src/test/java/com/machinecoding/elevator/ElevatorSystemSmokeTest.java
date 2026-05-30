package com.machinecoding.elevator;

import com.machinecoding.elevator.controller.ElevatorController;
import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.strategy.NearestCarStrategy;

import java.util.List;

public class ElevatorSystemSmokeTest {
    public static void main(String[] args) {
        ElevatorController.resetInstance();

        List<Elevator> elevators = List.of(
                new Elevator(1, 0, 10),
                new Elevator(2, 0, 10)
        );
        ElevatorController controller = ElevatorController.getInstance(
                elevators,
                new NearestCarStrategy()
        );

        int elevatorId = controller.submitExternalRequest(new ExternalRequest(5, Direction.UP));
        controller.submitInternalRequest(new InternalRequest(elevatorId, 8));

        while (controller.hasPendingRequests()) {
            controller.step();
        }

        Elevator assignedElevator = controller.getElevators().stream()
                .filter(elevator -> elevator.getId() == elevatorId)
                .findFirst()
                .orElseThrow();

        if (assignedElevator.getCurrentFloor() != 8) {
            throw new AssertionError("Expected elevator to finish at floor 8");
        }

        System.out.println("Smoke test passed");
    }
}
