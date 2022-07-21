package me.lozm.app.contract.vo;

import lombok.*;
import me.lozm.app.contract.code.TokenStatus;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractListVo {

    @Getter
    public static class Request {
        private final String privateKey;

        public Request(String privateKey) {
            Assert.hasLength(privateKey, "요청자의 개인키 정보는 비어있을 수 없습니다.");

            this.privateKey = privateKey;
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Response {
        private final List<Detail> tokenList;
    }

    @Getter
    @ToString
    public static class Detail {
        private final BigInteger tokenId;
        private final String tokenUrl;
        private final BigInteger tokenPrice;
        private final TokenStatus tokenStatus;

        public Detail(BigInteger tokenId, String tokenUrl, BigInteger tokenPrice) {
            this.tokenId = tokenId;
            this.tokenUrl = tokenUrl;
            this.tokenPrice = tokenPrice;
            this.tokenStatus = isNotEmpty(this.tokenPrice) && this.tokenPrice.compareTo(BigInteger.ZERO) > 0 ?
                    TokenStatus.SALE : TokenStatus.NOT_SALE;
        }
    }

}
