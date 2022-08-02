package me.lozm.app.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

import static me.lozm.global.utils.SwaggerUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractMintDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MintRequest {
        @Schema(description = LOGIN_ID_DESC, example = SELLER_LOGIN_ID_EXAMPLE)
        @NotBlank
        private String loginId;
        @Schema(description = PASSWORD_DESC, example = PASSWORD_EXAMPLE)
        @NotBlank
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MintResponse {
        @Schema(description = TOKEN_URL_DESC)
        private final String tokenUrl;
    }

}
