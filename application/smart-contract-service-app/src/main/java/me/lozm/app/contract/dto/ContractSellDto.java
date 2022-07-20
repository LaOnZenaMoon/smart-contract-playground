package me.lozm.app.contract.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractSellDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        @NotBlank
        private String privateKey;
        @NotBlank
        private String tokenPrice;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Response {
        private final String transactionHash;
    }

}
