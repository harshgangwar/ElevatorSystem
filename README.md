# Elevator System Machine Coding Project

A complete, self-contained Java implementation of an elevator dispatch system with:

- State pattern for elevator lifecycle behavior
- Strategy pattern for assigning requests to elevators
- Observer pattern for displays and monitoring
- Thread-safe scheduling and request queues
- One worker thread per elevator
- Separate priority queues for upward and downward stops
- Graceful startup/shutdown for demos and tests
- Simple deadlock-avoidance rules documented in code and design notes

The project uses only the JDK. No Maven or Gradle installation is required.

## Project Structure

```text
elevator-system/
├── README.md
├── pom.xml
└── src/
    ├── main/java/com/machinecoding/elevator/
    │   ├── Main.java
    │   ├── concurrency/
    │   ├── controller/
    │   ├── model/
    │   ├── observer/
    │   ├── service/
    │   ├── state/
    │   └── strategy/
    └── test/java/com/machinecoding/elevator/
        └── ElevatorSystemSmokeTest.java
```

## Run

From this directory:

```bash
javac -d out $(find src/main/java src/test/java -name "*.java")
java -cp out com.machinecoding.elevator.Main
java -cp out com.machinecoding.elevator.ElevatorSystemSmokeTest
```

If Maven is available on another machine, this project also includes a minimal `pom.xml`:

```bash
mvn compile
mvn exec:java
```

## Design Notes

### State Pattern

Each elevator owns a current `ElevatorState`. The worker loop repeatedly calls `state.handle(elevator)`, and the state decides the next transition:

- `IdleState`
- `MovingUpState`
- `MovingDownState`
- `DoorOpenState`
- `MaintenanceState`

### Strategy Pattern

`SchedulingStrategy` is swappable at runtime through `ElevatorController#setSchedulingStrategy`.

Implemented strategies:

- `NearestCarStrategy`
- `ScanStrategy`
- `ZoningStrategy`

### Observer Pattern

Observers subscribe to elevator events:

- `DisplayPanel`
- `MonitoringSystem`

The elevator uses `CopyOnWriteArrayList` so observers can be added while workers are running.

### Concurrency

- Each elevator runs in its own worker thread.
- Requests are stored in `PriorityBlockingQueue`.
- Shared elevator fields use `volatile` for visibility.
- Per-elevator mutation is guarded by a `ReentrantLock`.
- Controller assignment uses a separate short-lived lock.

### Deadlock Avoidance

The code follows a simple lock policy:

- The controller lock is used only to choose an elevator and enqueue the request.
- Elevator locks are never acquired while holding another elevator lock.
- Worker threads do not acquire the controller lock.
- Observer callbacks are fired after local state is updated and never require controller locks.

That keeps lock ownership shallow and prevents circular waits.
