package com.example.demo.exception;

public class GlobalException extends Exception {

    private static final long serialVersionUID = 5819174480253773214L;


    private int code;

    private String message;

    private Throwable throwable;

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public GlobalException( Throwable throwable) {
        super(throwable);
        this.throwable = throwable;
    }

    public GlobalException(int code, String message, Throwable throwable) {
        super(message,throwable);
        this.code = code;
        this.message = message;
        this.throwable = throwable;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

