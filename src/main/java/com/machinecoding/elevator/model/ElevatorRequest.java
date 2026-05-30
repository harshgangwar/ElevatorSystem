package com.machinecoding.elevator.model;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public abstract class ElevatorRequest {
    private static final AtomicLong SEQUENCE_GENERATOR = new AtomicLong(1);

    private final long requestId;
    private final int floor;
    private final Instant createdAt;

    protected ElevatorRequest(int floor) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor cannot be negative");
        }
        this.requestId = SEQUENCE_GENERATOR.getAndIncrement();
        this.floor = floor;
        this.createdAt = Instant.now();
    }

    public long getRequestId() {
        return requestId;
    }

    public int getFloor() {
        return floor;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{requestId=" + requestId
                + ", floor=" + floor
                + ", createdAt=" + createdAt
                + '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ElevatorRequest that)) {
            return false;
        }
        return requestId == that.requestId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}

