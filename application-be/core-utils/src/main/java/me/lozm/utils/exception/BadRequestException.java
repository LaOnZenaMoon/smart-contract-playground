package me.lozm.utils.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private CustomExceptionType type;


    public BadRequestException(CustomExceptionType type) {
        super();
        this.type = type;
    }

    public BadRequestException(CustomExceptionType type, String message) {
        super(message);
        this.type = type;
    }

}

