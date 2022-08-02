package me.lozm.app.contract.vo;

import lombok.*;
import me.lozm.app.contract.code.TokenSearchType;
import me.lozm.app.contract.code.TokenStatus;
import org.modelmapper.internal.util.Assert;

import java.math.BigInteger;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractListVo {

    @Getter
    public static class Request {
        private final TokenSearchType tokenSearchType;
        private final String walletAddress;

        public Request(TokenSearchType tokenSearchType, String walletAddress) {
            this.tokenSearchType = isEmpty(tokenSearchType) ? TokenSearchType.ON_SALE : tokenSearchType;
            this.walletAddress = walletAddress;

            Assert.isTrue(!validateWhenTokenSearchTypeIsPrivate(),
                    format("토큰 검색 조회 유형이 %s 일 때, 개인키 정보는 비어있을 수 없습니다.", TokenSearchType.PRIVATE.getCode()));
        }

        private boolean validateWhenTokenSearchTypeIsPrivate() {
            return this.tokenSearchType == TokenSearchType.PRIVATE && isBlank(walletAddress);
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
