package com.hzoom.common.error;

import org.slf4j.helpers.MessageFormatter;

public class ErrorException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private IError error;

    private ErrorException(IError error, String message, Throwable throwable) {
        super(message, throwable);
        this.error = error;
    }

    private ErrorException(IError error, String message) {
        super(message);
        this.error = error;
    }

    public IError getError(){
        return error;
    }

    public static Builder newBuilder(IError error){
        return new Builder(error);
    }

    public static class Builder {
        private IError error;
        private String message;
        private Throwable throwable;

        public Builder(IError error){
            this.error = error;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder message(String format, Object... args) {
            this.message = MessageFormatter.arrayFormat(format, args).getMessage();
            return this;
        }

        public Builder causeBy(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public ErrorException build(){
            String msg = this.error.toString();
            StringBuilder str = new StringBuilder(msg);
            if (this.message != null) {
                str.append("   ").append(this.message);
            }
            if (this.throwable == null) {
                return new ErrorException(this.error, str.toString());
            } else {
                return new ErrorException(this.error, str.toString(), this.throwable);
            }
        }
    }
}
