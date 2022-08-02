package me.lozm.utils.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {

    private CustomExceptionType type;


    public InternalServerException(CustomExceptionType type) {
        super(type.getMessage());
        this.type = type;
    }

    public InternalServerException(CustomExceptionType type, Throwable cause) {
        super(type.getMessage(), cause);
        this.type = type;
    }

    public InternalServerException(CustomExceptionType type, String message) {
        super(message);
        this.type = type;
    }

}
