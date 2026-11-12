package com.jlmasa.pms_backend.assignment.service;

public class InvalidTimerOperationException extends RuntimeException{

    public InvalidTimerOperationException(String message){
        super(message);
    }
}
