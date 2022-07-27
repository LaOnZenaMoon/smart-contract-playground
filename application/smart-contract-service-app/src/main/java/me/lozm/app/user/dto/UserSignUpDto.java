package me.lozm.app.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static me.lozm.global.utils.SwaggerUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpDto {

    @Getter
    @NoArgsConstructor
    public static class SignUpRequest {
        @Schema(description = LOGIN_ID_DESC, example = LOGIN_ID_EXAMPLE)
        @NotBlank
        private String loginId;
        @Schema(description = PASSWORD_DESC, example = PASSWORD_EXAMPLE)
        @NotBlank
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SignUpResponse {
        @Schema(description = PRIVATE_KEY_DESC)
        private final String privateKey;
        @Schema(description = MNEMONIC_DESC)
        private final String mnemonic;
        @Schema(description = WALLET_ADDRESS_DESC)
        private final String walletAddress;
    }

}
