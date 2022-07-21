package me.lozm.app.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import me.lozm.app.contract.code.TokenStatus;

import java.math.BigInteger;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractListDto {

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListResponse {
        private final List<ListDetail> tokenList;
    }

    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDetail {
        private final BigInteger tokenId;
        private final String tokenUrl;
        private final BigInteger tokenPrice;
        private final TokenStatus tokenStatus;

        @Builder
        public ListDetail(BigInteger tokenId, String tokenUrl, BigInteger tokenPrice, TokenStatus tokenStatus) {
            this.tokenId = tokenId;
            this.tokenUrl = tokenUrl;
            this.tokenPrice = tokenPrice;
            this.tokenStatus = tokenStatus;
        }
    }

}
