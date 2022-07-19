package me.lozm.utils.exception;

import lombok.Getter;

@Getter
public enum CustomExceptionType {

    // Common
    SUCCESS("OK", ""),
    INVALID_REQUEST_PARAMETERS("PARAMS_001", "요청값이 잘못되었습니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR("SERVER_001", "요청을 처리하는 중에 오류가 있습니다."),
    INTERNAL_SERVER_ERROR_IPFS("SERVER_002", "IPFS 요청을 처리하는 중에 오류가 있습니다."),
    ;

    private final String code;
    private final String message;


    CustomExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
