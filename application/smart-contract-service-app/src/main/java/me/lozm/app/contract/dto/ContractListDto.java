package me.lozm.app.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractListDto {

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        private final List<Detail> tokenList;
    }

    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        private final BigInteger tokenId;
        private final String tokenUrl;
        private final BigInteger tokenPrice;

        public Detail(BigInteger tokenId, String tokenUrl, BigInteger tokenPrice) {
            this.tokenId = tokenId;
            this.tokenUrl = tokenUrl;
            this.tokenPrice = tokenPrice;
        }
    }

}
