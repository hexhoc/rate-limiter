package org.example.ratelimiter.controller;

public class TooManyRequestsException extends Exception {

    public TooManyRequestsException() {
        super("Too many requests");
    }
}
