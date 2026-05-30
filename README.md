# Elevator System LLD Interview Project

This is a deliberately simple Java elevator-system implementation for low-level
design and machine-coding interviews.

It keeps the important patterns visible:

- Models: `Elevator`, `Door`, `ExternalRequest`, `InternalRequest`
- State pattern: `IdleState`, `MovingUpState`, `MovingDownState`, `DoorOpenState`, `MaintenanceState`
- Strategy pattern: `NearestCarStrategy`, `ScanStrategy`, `ZoningStrategy`
- Observer pattern: `ElevatorObserver`, `DisplayPanel`, `MonitoringSystem`
- Singleton: `ElevatorController`
- Basic concurrency: important controller/elevator methods are `synchronized`

No background workers, thread pools, services, or production-style plumbing are used.

## Run

From `elevator-system`:

```bash
javac -d out $(find src/main/java src/test/java -name "*.java")
java -cp out com.machinecoding.elevator.Main
java -cp out com.machinecoding.elevator.ElevatorSystemSmokeTest
```

## Design

`ElevatorController` is a singleton. It accepts external/internal requests and
uses a pluggable `SchedulingStrategy` to pick an elevator.

Each `Elevator` owns two priority queues:

- `upStops`: min-heap, so lower upward floors are served first
- `downStops`: max-heap, so higher downward floors are served first

The elevator has a current `ElevatorState`. Calling `controller.step()` asks each
elevator to process one state transition. This keeps the simulation easy to read.

## Concurrency For Interviews

The locking model is intentionally basic:

- `submitExternalRequest` and `submitInternalRequest` are `synchronized`
- elevator mutation methods like `addStop`, `moveToFloor`, `openDoor`, and `closeDoor` are `synchronized`
- queues are plain `PriorityQueue` because access is protected by synchronized methods

In an interview, this is usually enough. You can mention that a production version
could use worker threads, blocking queues, and finer-grained locks.
