package me.lozm.global.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.lozm.utils.exception.CustomExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static me.lozm.utils.exception.DateUtils.DATETIME_PATTERN;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseDto<T> {

    private String code;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATETIME_PATTERN)
    private LocalDateTime responseDateTime;
    private T data;

    @Builder
    public CommonResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.responseDateTime = LocalDateTime.now();
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> ok() {
        CommonResponseDto<T> responseDto = CommonResponseDto.<T>builder()
                .code(HttpStatus.OK.name())
                .build();
        return getOkResponseEntity(responseDto);
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> ok(T object) {
        CommonResponseDto<T> responseDto = CommonResponseDto.<T>builder()
                .code(HttpStatus.OK.name())
                .data(object)
                .build();
        return getOkResponseEntity(responseDto);
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> created() {
        CommonResponseDto<T> responseDto = CommonResponseDto.<T>builder()
                .code(HttpStatus.CREATED.name())
                .build();
        return getCreatedResponseEntity(responseDto);
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> created(T object) {
        CommonResponseDto<T> responseDto = CommonResponseDto.<T>builder()
                .code(HttpStatus.CREATED.name())
                .data(object)
                .build();
        return getCreatedResponseEntity(responseDto);
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> badRequest(String message) {
        CommonResponseDto<T> response = CommonResponseDto.<T>builder()
                .code(CustomExceptionType.INVALID_REQUEST_PARAMETERS.getCode())
                .message(isEmpty(message) ? CustomExceptionType.INVALID_REQUEST_PARAMETERS.getMessage() : message)
                .build();
        return getBadRequestResponseEntity(response);
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> badRequest(CustomExceptionType type, String additionalMessage) {
        CommonResponseDto<T> response = CommonResponseDto.<T>builder()
                .code(type.getCode())
                .message(isEmpty(additionalMessage) ? type.getMessage() : format("%s %s", type.getMessage(), additionalMessage))
                .build();
        return getBadRequestResponseEntity(response);
    }
    public static <T> ResponseEntity<CommonResponseDto<T>> badRequest(CustomExceptionType type, String message, T data) {
        CommonResponseDto<T> response = CommonResponseDto.<T>builder()
                .code(type.getCode())
                .message(message)
                .data(data)
                .build();
        return getBadRequestResponseEntity(response);
    }

    public static ResponseEntity<CommonResponseDto<List<FieldError>>> badRequest(CustomExceptionType type, BindingResult bindingResult) {
        CommonResponseDto<List<FieldError>> response = CommonResponseDto.<List<FieldError>>builder()
                .code(type.getCode())
                .message(type.getMessage())
                .data(FieldError.of(bindingResult))
                .build();
        return getBadRequestResponseEntity(response);
    }

    private static <T> ResponseEntity<CommonResponseDto<T>> getOkResponseEntity(CommonResponseDto<T> responseDto) {
        return ResponseEntity.ok(responseDto);
    }

    private static <T> ResponseEntity<CommonResponseDto<T>> getCreatedResponseEntity(CommonResponseDto<T> responseDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseDto);
    }

    private static <T> ResponseEntity<CommonResponseDto<T>> getBadRequestResponseEntity(CommonResponseDto<T> response) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}