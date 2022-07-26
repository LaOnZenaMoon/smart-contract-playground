package me.lozm.utils.exception;

import lombok.Getter;

@Getter
public enum CustomExceptionType {

    // Common
    SUCCESS("OK", ""),
    INVALID_REQUEST_PARAMETERS("PARAMS_001", "요청값이 잘못되었습니다."),
    ALREADY_EXIST_UPLOAD_FILE("PARAMS_002", "이미 등록된 파일입니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR("SERVER_001", "요청을 처리하는 중에 오류가 있습니다."),
    INTERNAL_SERVER_ERROR_IPFS("SERVER_002", "IPFS 요청을 처리하는 중에 오류가 있습니다."),
    INTERNAL_SERVER_ERROR_SMART_CONTRACT("SERVER_003", "스마트 컨트랙트 요청을 처리하는 중에 오류가 있습니다."),
    INTERNAL_SERVER_ERROR_WALLET("SERVER_003", "지갑 요청을 처리하는 중에 오류가 있습니다."),

    // User
    ALREADY_EXIST_LOGIN_ID("USER_001", "이미 존재하는 로그인 ID 입니다."),
    INVALID_USER_PASSWORD("USER_002", "사용자의 패스워드가 잘못되었습니다."),
    ;

    private final String code;
    private final String message;


    CustomExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
