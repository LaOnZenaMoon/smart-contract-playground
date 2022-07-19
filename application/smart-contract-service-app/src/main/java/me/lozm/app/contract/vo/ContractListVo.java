package me.lozm.app.contract.vo;

import lombok.*;
import org.springframework.util.Assert;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;
import java.util.List;

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
    public static class Detail extends DynamicStruct {
        private final BigInteger tokenId;
        private final String tokenUrl;
        private final BigInteger tokenPrice;

        public Detail(Uint256 tokenId, Utf8String tokenUrl, Uint256 tokenPrice) {
            super(tokenId, tokenUrl, tokenPrice);

            this.tokenId = tokenId.getValue();
            this.tokenUrl = tokenUrl.getValue();
            this.tokenPrice = tokenPrice.getValue();
        }
    }

}
