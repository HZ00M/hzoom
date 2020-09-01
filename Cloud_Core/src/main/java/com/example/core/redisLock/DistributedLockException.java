package com.example.core.redisLock;

public class DistributedLockException extends Exception {
    private String methodName;

    public DistributedLockException(String methodName, String message) {
        super(message);
        this.methodName = methodName;
    }

}
