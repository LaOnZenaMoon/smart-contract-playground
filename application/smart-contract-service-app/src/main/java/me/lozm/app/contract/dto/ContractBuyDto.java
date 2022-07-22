package me.lozm.app.contract.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractBuyDto {

    @Getter
    @NoArgsConstructor
    public static class BuyRequest {
        @NotBlank
        private String privateKey;
        @NotBlank
        private String tokenPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class BuyResponse {
        private final String transactionHash;
    }

}
