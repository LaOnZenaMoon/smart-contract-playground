package me.lozm.app.contract.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractPurchaseDto {

    @Getter
    @NoArgsConstructor
    public static class PurchaseRequest {
        @NotBlank
        private String privateKey;
        @NotBlank
        private String tokenPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class PurchaseResponse {
        private final String transactionHash;
    }

}
