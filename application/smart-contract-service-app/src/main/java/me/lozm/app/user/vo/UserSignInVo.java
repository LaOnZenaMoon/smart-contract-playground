package me.lozm.app.user.vo;

import lombok.*;
import org.springframework.util.Assert;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignInVo {

    @Getter
    public static class Request {
        private final String loginId;
        private final String password;

        public Request(String loginId, String password) {
            Assert.hasLength(loginId, "로그인 아이디는 비어있을 수 없습니다.");
            Assert.hasLength(password, "비밀번호는 비어있을 수 없습니다.");

            this.loginId = loginId;
            this.password = password;
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class Response {
        private final String walletAddress;
    }

}
