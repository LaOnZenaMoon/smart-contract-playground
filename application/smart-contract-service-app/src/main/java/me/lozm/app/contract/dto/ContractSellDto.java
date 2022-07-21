package me.lozm.app.contract.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractSellDto {

    @Getter
    @NoArgsConstructor
    public static class SellRequest {
        @NotBlank
        private String privateKey;
        @NotBlank
        private String tokenPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class SellResponse {
        private final String transactionHash;
    }

}
