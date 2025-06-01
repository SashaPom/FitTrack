package com.duikt.fittrack.exception;

public class WorkoutAlreadyExistsException extends RuntimeException {
    public WorkoutAlreadyExistsException(String message) {
        super(message);
    }
}
