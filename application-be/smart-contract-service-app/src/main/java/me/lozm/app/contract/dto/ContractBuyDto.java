package me.lozm.app.contract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

import static me.lozm.global.utils.SwaggerUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractBuyDto {

    @Getter
    @NoArgsConstructor
    public static class BuyRequest {
        @Schema(description = LOGIN_ID_DESC, example = BUYER_LOGIN_ID_EXAMPLE)
        @NotBlank
        private String loginId;
        @Schema(description = PASSWORD_DESC, example = PASSWORD_EXAMPLE)
        @NotBlank
        private String password;
        @Schema(description = TOKEN_PRICE_DESC, example = TOKEN_PRICE_EXAMPLE)
        @NotBlank
        private String tokenPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class BuyResponse {
        @Schema(description = TRANSACTION_DESC)
        private final String transactionHash;
    }

}
