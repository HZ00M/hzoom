package com.example.core.redis;

public class DistributedLockException extends Exception {
    private String methodName;

    public DistributedLockException(String methodName, String message) {
        super(message);
        this.methodName = methodName;
    }

}
