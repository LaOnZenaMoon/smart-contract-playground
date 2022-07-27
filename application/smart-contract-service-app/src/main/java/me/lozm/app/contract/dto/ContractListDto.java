package me.lozm.app.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.lozm.app.contract.code.TokenStatus;

import java.math.BigInteger;
import java.util.List;

import static me.lozm.global.utils.SwaggerUtils.*;

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
        @Schema(description = TOKEN_ID_DESC)
        private final BigInteger tokenId;
        @Schema(description = TOKEN_URL_DESC)
        private final String tokenUrl;
        @Schema(description = TOKEN_PRICE_DESC)
        private final BigInteger tokenPrice;
        @Schema(description = TOKEN_STATUS_DESC)
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
