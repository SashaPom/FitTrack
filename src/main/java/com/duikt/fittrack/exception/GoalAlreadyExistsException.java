package com.duikt.fittrack.exception;

public class GoalAlreadyExistsException extends RuntimeException{
    public GoalAlreadyExistsException(String message) {
        super(message);
    }
}
