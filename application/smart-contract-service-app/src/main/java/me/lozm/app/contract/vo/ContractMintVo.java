package me.lozm.app.contract.vo;

import lombok.*;
import org.springframework.util.Assert;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContractMintVo {

    @Getter
    public static class Request {
        private final String loginId;
        private final String password;
        private final File uploadFile;

        public Request(String loginId, String password, File uploadFile) {
            Assert.hasLength(loginId, "요청자의 개인키 정보는 비어있을 수 없습니다.");
            Assert.hasLength(password, "요청자의 개인키 정보는 비어있을 수 없습니다.");
            Assert.notNull(uploadFile, "등록할 NFT 파일은 null 일 수 없습니다.");

            this.loginId = loginId;
            this.password = password;
            this.uploadFile = uploadFile;
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Response {
        private final String tokenUrl;
    }

}
