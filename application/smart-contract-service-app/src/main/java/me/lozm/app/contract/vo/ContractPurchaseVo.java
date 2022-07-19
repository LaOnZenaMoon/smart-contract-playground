package me.lozm.app.contract.vo;

import lombok.*;
import me.lozm.utils.exception.BadRequestException;
import me.lozm.utils.exception.CustomExceptionType;
import org.springframework.util.Assert;

import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractPurchaseVo {

    @Getter
    public static class Request {
        private final String privateKey;
        private final BigInteger tokenId;
        private final BigInteger tokenPrice;

        public Request(String privateKey, String tokenId, String tokenPrice) {
            Assert.hasLength(privateKey, "요청자의 개인키 정보는 비어있을 수 없습니다.");
            Assert.hasLength(tokenId, "token ID 는 비어있을 수 없습니다.");
            Assert.notNull(tokenPrice, "token 가격은 null 일 수 없습니다.");

            this.privateKey = privateKey;
            this.tokenId = createBigInteger(tokenId);
            this.tokenPrice = createBigInteger(tokenPrice);

            Assert.isTrue(this.tokenPrice.compareTo(BigInteger.ZERO) > 0, "token 가격은 0 보다 커야합니다.");
        }

        private BigInteger createBigInteger(String value) {
            try {
                return new BigInteger(value);
            } catch (NumberFormatException e) {
                throw new BadRequestException(CustomExceptionType.INVALID_REQUEST_PARAMETERS);
            }
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Response {
        private final String transactionHash;
    }

}
