package me.lozm.app.contract.vo;

import lombok.*;
import me.lozm.utils.exception.BadRequestException;
import me.lozm.utils.exception.CustomExceptionType;
import org.springframework.util.Assert;

import java.math.BigInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractBuyVo {

    @Getter
    public static class Request {
        private final String loginId;
        private final String password;
        private final BigInteger tokenId;
        private final BigInteger tokenPrice;

        public Request(String loginId, String password, String tokenId, String tokenPrice) {
            Assert.hasLength(loginId, "요청자의 로그인 ID는 비어있을 수 없습니다.");
            Assert.hasLength(password, "요청자의 비밀번호는 비어있을 수 없습니다.");
            Assert.hasLength(tokenId, "token ID 는 비어있을 수 없습니다.");
            Assert.notNull(tokenPrice, "token 가격은 null 일 수 없습니다.");

            this.loginId = loginId;
            this.password = password;
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
