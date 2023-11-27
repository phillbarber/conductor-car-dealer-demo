package com.github.phillbarber.conductor.remoteservices;

public record OrderValidationResponse (String rejectionMessage, boolean isValid) {

}
