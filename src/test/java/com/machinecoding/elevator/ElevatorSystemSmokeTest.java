package com.machinecoding.elevator;

import com.machinecoding.elevator.concurrency.ElevatorExecutor;
import com.machinecoding.elevator.controller.ElevatorController;
import com.machinecoding.elevator.model.Direction;
import com.machinecoding.elevator.model.Elevator;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;
import com.machinecoding.elevator.service.RequestProcessor;
import com.machinecoding.elevator.strategy.NearestCarStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ElevatorSystemSmokeTest {
    public static void main(String[] args) throws Exception {
        List<Elevator> elevators = List.of(
                new Elevator(1, 0, 12),
                new Elevator(2, 0, 12)
        );
        ElevatorController controller = new ElevatorController(elevators, new NearestCarStrategy());
        RequestProcessor processor = new RequestProcessor(controller);

        try (ElevatorExecutor executor = new ElevatorExecutor(elevators.size())) {
            executor.start(elevators);

            int assignedElevator = processor.process(new ExternalRequest(5, Direction.UP));
            processor.process(new InternalRequest(assignedElevator, 9));
            processor.process(new ExternalRequest(2, Direction.DOWN));

            waitUntilNoPendingStops(controller, 6_000);

            boolean anyElevatorMoved = controller.snapshots().stream()
                    .anyMatch(snapshot -> snapshot.currentFloor() != snapshot.minFloor());
            if (!anyElevatorMoved) {
                throw new AssertionError("Expected at least one elevator to move");
            }

            controller.putInMaintenance(1);
            boolean rejected = false;
            try {
                processor.process(new InternalRequest(1, 3));
            } catch (IllegalStateException expected) {
                rejected = true;
            }
            if (!rejected) {
                throw new AssertionError("Expected maintenance elevator to reject new stops");
            }

            System.out.println("Smoke test passed");
        }
    }

    private static void waitUntilNoPendingStops(ElevatorController controller, long timeoutMillis)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            boolean allDone = controller.snapshots().stream()
                    .allMatch(snapshot -> snapshot.pendingStops() == 0);
            if (allDone) {
                TimeUnit.MILLISECONDS.sleep(600);
                return;
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
        throw new AssertionError("Timed out waiting for elevators to finish queued stops");
    }
}

