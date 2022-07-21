package me.lozm.app.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractMintDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MintRequest {
        @NotBlank
        private String privateKey;
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MintResponse {
        private final String tokenUrl;
    }

}
