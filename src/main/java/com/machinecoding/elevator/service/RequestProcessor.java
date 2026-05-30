package com.machinecoding.elevator.service;

import com.machinecoding.elevator.controller.ElevatorController;
import com.machinecoding.elevator.model.ExternalRequest;
import com.machinecoding.elevator.model.InternalRequest;

import java.util.Objects;

public class RequestProcessor {
    private final ElevatorController controller;

    public RequestProcessor(ElevatorController controller) {
        this.controller = Objects.requireNonNull(controller);
    }

    public int process(ExternalRequest request) {
        return controller.submitExternalRequest(request);
    }

    public void process(InternalRequest request) {
        controller.submitInternalRequest(request);
    }
}

